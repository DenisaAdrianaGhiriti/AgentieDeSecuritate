package com.securitate.agentie.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.securitate.agentie.backend.dto.CreateProcesVerbalRequest;
import com.securitate.agentie.backend.model.Eveniment;
import com.securitate.agentie.backend.model.Pontaj;
import com.securitate.agentie.backend.model.Post;
import com.securitate.agentie.backend.model.ProcesVerbal;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.repository.PontajRepository;
import com.securitate.agentie.backend.repository.ProcesVerbalRepository;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class ProcesVerbalService {

    private final PontajRepository pontajRepository;
    private final ProcesVerbalRepository procesVerbalRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Path pdfRootDir = Paths.get("uploads/procese-verbale");

    private static final Font FONT_NORMAL = new Font(Font.HELVETICA, 12, Font.NORMAL);
    private static final Font FONT_BOLD = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font FONT_TITLE = new Font(Font.HELVETICA, 14, Font.BOLD);

    public ProcesVerbalService(PontajRepository pontajRepository, ProcesVerbalRepository procesVerbalRepository) {
        this.pontajRepository = pontajRepository;
        this.procesVerbalRepository = procesVerbalRepository;

        try {
            Files.createDirectories(this.pdfRootDir);
        } catch (IOException e) {
            System.err.println("--- NU S-A PUTUT CREA DIRECTORUL PENTRU PDF-uri (procese-verbale) ---");
            e.printStackTrace();
        }
    }

    /**
     * Returnăm DTO ca să evităm probleme cu entități LAZY la JSON.
     */
    public ProcesVerbalResponse createProcesVerbal(Long pontajId, CreateProcesVerbalRequest request, User paznic) throws IOException {
        if (paznic == null) throw new IllegalStateException("Utilizator neautentificat.");
        if (pontajId == null) throw new IllegalArgumentException("pontajId este obligatoriu.");
        if (request == null) throw new IllegalArgumentException("Request invalid (null).");

        // Validări request (ore + evenimente)
        requireNotNull(request.getOra_declansare_alarma(), "Ora declanșare alarmă este obligatorie.");
        requireNotNull(request.getOra_prezentare_echipaj(), "Ora prezentare echipaj este obligatorie.");
        requireNotNull(request.getOra_incheiere_misiune(), "Ora încheiere misiune este obligatorie.");

        List<Eveniment> evenimente = request.getEvenimente();
        if (evenimente == null) {
            throw new IllegalArgumentException("Lista de evenimente este obligatorie (poate fi goală, dar nu null).");
        }

        // Pontaj
        Pontaj pontaj = pontajRepository.findById(pontajId)
                .orElseThrow(() -> new IllegalArgumentException("Pontajul asociat nu a fost găsit."));

        if (pontaj.getPaznic() == null || !Objects.equals(pontaj.getPaznic().getId(), paznic.getId())) {
            throw new SecurityException("Nu sunteți autorizat pentru acest pontaj.");
        }

        // Beneficiar + Post (obiectiv)
        User beneficiary = pontaj.getBeneficiary();
        if (beneficiary == null) {
            throw new IllegalStateException("Pontajul nu are beneficiar asociat.");
        }

        // La tine, User NU are numeCompanie/numeFirma direct, doar Profile embedded.
        // Deci folosim doar profile.numeFirma.
        String numeFirma = "Beneficiar (nume firmă lipsă)";
        if (beneficiary.getProfile() != null && !isBlank(beneficiary.getProfile().getNumeFirma())) {
            numeFirma = beneficiary.getProfile().getNumeFirma().trim();
        }

        Post post = pontaj.getPost();
        if (post == null) {
            throw new IllegalStateException("Pontajul nu are post (obiectiv) asociat.");
        }

        String denumireObiectiv = safe(post.getNumePost());
        String adresaObiectiv = safe(post.getAdresaPost());

        // Cale fișier PDF
        String fileName = "PV_" + pontajId + "_" + System.currentTimeMillis() + ".pdf";
        Path filePath = this.pdfRootDir.resolve(fileName);

        // IMPORTANT: cale web (pentru frontend)
        String caleStocareRelativa = "/uploads/procese-verbale/" + fileName;

        // Generare PDF
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toString()));
            document.open();

            // TITLU
            Paragraph titlu = new Paragraph("PROCES – VERBAL", FONT_TITLE);
            titlu.setAlignment(Element.ALIGN_CENTER);
            document.add(titlu);
            document.add(new Paragraph("\n\n", FONT_NORMAL));

            document.add(new Paragraph("Incheiat astazi " + DATE_FORMATTER.format(LocalDate.now()), FONT_NORMAL));
            document.add(new Paragraph("\n", FONT_NORMAL));

            // Obiectiv = firma + post + adresa
            document.add(new Paragraph("La obiectivul (denumirea si adresa):", FONT_NORMAL));
            document.add(new Paragraph(
                    numeFirma + " - " + denumireObiectiv + (adresaObiectiv.isEmpty() ? "" : (", " + adresaObiectiv)),
                    FONT_BOLD
            ));
            document.add(new Paragraph("\n", FONT_NORMAL));

            // Paznic
            Paragraph pPaznic = new Paragraph("de ", FONT_NORMAL);
            pPaznic.add(new Paragraph(paznic.getNume() + " " + paznic.getPrenume(), FONT_BOLD));
            pPaznic.add(new Paragraph(", reprezentant al echipei de interventie, ca urmare a verificarii timpilor de interventie la obiectivele monitorizate.", FONT_NORMAL));
            document.add(pPaznic);
            document.add(new Paragraph("\n", FONT_NORMAL));

            // Ore
            document.add(makeOraLine("Alarma a fost declansata la ora ", request.getOra_declansare_alarma()));
            document.add(new Paragraph("\n", FONT_NORMAL));

            document.add(makeOraLine("Echipajul de interventie s-a prezentat la ora ", request.getOra_prezentare_echipaj()));
            document.add(new Paragraph("\n", FONT_NORMAL));

            String observatii = request.getObservatii_generale();
            if (isBlank(observatii)) {
                observatii = "In timpul interventiei echipajul de interventie a actionat conform procedurilor in vigoare, neexistand observatii din partea beneficiarului privind calitatea prestatiei.";
            }
            document.add(new Paragraph(observatii, FONT_NORMAL));
            document.add(new Paragraph("\n", FONT_NORMAL));

            document.add(makeOraLine("Misiunea s-a incheiat la ora ", request.getOra_incheiere_misiune()));
            document.add(new Paragraph("\n", FONT_NORMAL));

            document.add(new Paragraph("Drept pentru care am incheiat prezentul proces-verbal in 2 (doua) exemplare.", FONT_NORMAL));
            document.add(new Paragraph("\n\n\n\n\n", FONT_NORMAL));

            // Semnături
            PdfPTable semanturi = new PdfPTable(2);
            semanturi.setWidthPercentage(100);

            PdfPCell cellStanga = new PdfPCell(new Paragraph("ECHIPA DE INTERVENTIE\n____________________", FONT_NORMAL));
            cellStanga.setBorder(Rectangle.NO_BORDER);

            PdfPCell cellDreapta = new PdfPCell(new Paragraph("BENEFICIAR\n____________________", FONT_NORMAL));
            cellDreapta.setBorder(Rectangle.NO_BORDER);
            cellDreapta.setHorizontalAlignment(Element.ALIGN_RIGHT);

            semanturi.addCell(cellStanga);
            semanturi.addCell(cellDreapta);
            document.add(semanturi);

            // Pagina 2: tabel evenimente
            document.newPage();

            float[] columnWidths = {30, 80, 70, 60, 50, 70, 80, 80};
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            addHeaderCell(table, "Nr. crt.");
            addHeaderCell(table, "Data/Ora Rec.");
            addHeaderCell(table, "Tip Alarma");
            addHeaderCell(table, "Echipaj");
            addHeaderCell(table, "Ora Sosirii");
            addHeaderCell(table, "Cauze");
            addHeaderCell(table, "Solutionare");
            addHeaderCell(table, "Observatii");

            int nrCrt = 1;
            for (Eveniment ev : evenimente) {
                table.addCell(new Paragraph(String.valueOf(nrCrt++), FONT_NORMAL));
                table.addCell(new Paragraph(ev.getDataOraReceptionarii() != null ? DATETIME_FORMATTER.format(ev.getDataOraReceptionarii()) : "", FONT_NORMAL));
                table.addCell(new Paragraph(safe(ev.getTipulAlarmei()), FONT_NORMAL));
                table.addCell(new Paragraph(safe(ev.getEchipajAlarmat()), FONT_NORMAL));
                table.addCell(new Paragraph(ev.getOraSosirii() != null ? TIME_FORMATTER.format(ev.getOraSosirii()) : "", FONT_NORMAL));
                table.addCell(new Paragraph(safe(ev.getCauzeleAlarmei()), FONT_NORMAL));
                table.addCell(new Paragraph(safe(ev.getModulDeSolutionare()), FONT_NORMAL));
                table.addCell(new Paragraph(safe(ev.getObservatii()), FONT_NORMAL));
            }

            document.add(table);

        } catch (Exception e) {
            throw new IOException("Eroare la generarea PDF-ului: " + e.getMessage(), e);
        } finally {
            if (document.isOpen()) document.close();
        }

        // Salvare DB
        ProcesVerbal newProcesVerbal = new ProcesVerbal();
        newProcesVerbal.setPontaj(pontaj);
        newProcesVerbal.setPaznic(paznic);
        newProcesVerbal.setPost(post);

        newProcesVerbal.setReprezentantBeneficiar(request.getReprezentant_beneficiar());
        newProcesVerbal.setOraDeclansareAlarma(request.getOra_declansare_alarma());
        newProcesVerbal.setOraPrezentareEchipaj(request.getOra_prezentare_echipaj());
        newProcesVerbal.setOraIncheiereMisiune(request.getOra_incheiere_misiune());
        newProcesVerbal.setObservatiiGenerale(request.getObservatii_generale());
        newProcesVerbal.setEvenimente(evenimente);
        newProcesVerbal.setCaleStocarePDF(caleStocareRelativa);

        ProcesVerbal saved = procesVerbalRepository.save(newProcesVerbal);

        return new ProcesVerbalResponse(
                saved.getId(),
                saved.getCaleStocarePDF(),
                saved.getCreatedAt(),
                numeFirma,
                denumireObiectiv,
                adresaObiectiv
        );
    }

    private Paragraph makeOraLine(String prefix, LocalDateTime dt) {
        Paragraph p = new Paragraph(prefix, FONT_NORMAL);
        p.add(new Paragraph(TIME_FORMATTER.format(dt), FONT_BOLD));
        return p;
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Paragraph(text, FONT_BOLD));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }

    private static void requireNotNull(Object value, String message) {
        if (value == null) throw new IllegalArgumentException(message);
    }

    @SuppressWarnings("unused")
    private static void requireNotBlank(String value, String message) {
        if (isBlank(value)) throw new IllegalArgumentException(message);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    // DTO simplu pentru response
    public static class ProcesVerbalResponse {
        private final Long id;
        private final String caleStocarePDF;
        private final LocalDateTime createdAt;

        private final String numeFirma;
        private final String numePost;
        private final String adresaPost;

        public ProcesVerbalResponse(Long id, String caleStocarePDF, LocalDateTime createdAt,
                                    String numeFirma, String numePost, String adresaPost) {
            this.id = id;
            this.caleStocarePDF = caleStocarePDF;
            this.createdAt = createdAt;
            this.numeFirma = numeFirma;
            this.numePost = numePost;
            this.adresaPost = adresaPost;
        }

        public Long getId() { return id; }
        public String getCaleStocarePDF() { return caleStocarePDF; }
        public LocalDateTime getCreatedAt() { return createdAt; }

        public String getNumeFirma() { return numeFirma; }
        public String getNumePost() { return numePost; }
        public String getAdresaPost() { return adresaPost; }
    }
}
