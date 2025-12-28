package com.securitate.agentie.backend.service;

// --- IMPORTURILE SUNT DIFERITE! Folosim com.lowagie ---
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
// --- Sfârșit importuri noi ---

import com.securitate.agentie.backend.dto.CreateProcesVerbalRequest;
import com.securitate.agentie.backend.model.*;
import com.securitate.agentie.backend.repository.PontajRepository;
import com.securitate.agentie.backend.repository.ProcesVerbalRepository;
import org.springframework.stereotype.Service;

import java.awt.Color; // Avem nevoie și de AWT pentru culori
import java.io.FileOutputStream; // Folosim FileOutputStream
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class ProcesVerbalService {

    private final PontajRepository pontajRepository;
    private final ProcesVerbalRepository procesVerbalRepository;

    // Formatoarele rămân la fel
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Path pdfRootDir = Paths.get("uploads/procese-verbale");

    // Fonturi pe care le vom folosi (stilul vechi iText)
    private static final Font FONT_NORMAL = new Font(Font.HELVETICA, 12, Font.NORMAL);
    private static final Font FONT_BOLD = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font FONT_TITLE = new Font(Font.HELVETICA, 14, Font.BOLD);


    public ProcesVerbalService(PontajRepository pontajRepository, ProcesVerbalRepository procesVerbalRepository) {
        this.pontajRepository = pontajRepository;
        this.procesVerbalRepository = procesVerbalRepository;

        try {
            Files.createDirectories(this.pdfRootDir);
        } catch (IOException e) {
            System.err.println("--- NU S-A PUTUT CREA DIRECTORUL PENTRU PDF-uri ---");
            e.printStackTrace();
        }
    }

    public ProcesVerbal createProcesVerbal(Long pontajId, CreateProcesVerbalRequest request, User paznic) throws IOException {

        // --- PASUL 1: Validarea (rămâne la fel) ---
        Pontaj pontaj = pontajRepository.findById(pontajId)
                .orElseThrow(() -> new IllegalArgumentException("Pontajul asociat nu a fost găsit."));

        if (!Objects.equals(pontaj.getPaznic().getId(), paznic.getId())) {
            throw new SecurityException("Nu sunteți autorizat pentru acest pontaj.");
        }

        // --- PASUL 2: Calea fișierului (rămâne la fel) ---
        String fileName = "PV_" + pontajId + "_" + System.currentTimeMillis() + ".pdf";
        Path filePath = this.pdfRootDir.resolve(fileName);
        String caleStocareRelativa = "/uploads/procese-verbale/" + fileName;

        // --- PASUL 3: Generarea PDF cu OpenPDF ---
        Document document = new Document(PageSize.A4);

        try {
            // Sintaxa de creare a writer-ului este diferită
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toString()));

            // Trebuie să deschidem documentul manual
            document.open();

            // --- PAGINA 1: Textul procesului verbal ---
            Paragraph titlu = new Paragraph("PROCES – VERBAL", FONT_TITLE);
            titlu.setAlignment(Element.ALIGN_CENTER);
            document.add(titlu);
            document.add(new Paragraph("\n\n", FONT_NORMAL)); // Spațiu

            document.add(new Paragraph("Incheiat astazi " + DATE_FORMATTER.format(LocalDate.now()), FONT_NORMAL));
            document.add(new Paragraph("\n", FONT_NORMAL));

            // "La obiectivul..." (Paragrafele se construiesc puțin diferit)
            User beneficiary = pontaj.getBeneficiary();
            Paragraph pObiectiv = new Paragraph("La obiectivul (denumirea si adresa) ", FONT_NORMAL);
            // NOTĂ: Folosește câmpurile din entitatea User a Beneficiarului, adaptat la structura Profile
            String numeCompanie = beneficiary.getProfile().getNumeFirma(); // Sau getNumeCompanie() dacă e cazul
            String punctDeLucru = beneficiary.getProfile().getPuncteDeLucru().isEmpty()
                    ? "(Punct principal)"
                    : beneficiary.getProfile().getPuncteDeLucru().get(0); // Luăm primul punct de lucru ca exemplu.

            pObiectiv.add(new Paragraph(numeCompanie + ", " + punctDeLucru, FONT_BOLD));
            pObiectiv.add(new Paragraph(",", FONT_NORMAL));
            document.add(pObiectiv);
            document.add(new Paragraph("\n", FONT_NORMAL));

            // "de..."
            Paragraph pPaznic = new Paragraph("de ", FONT_NORMAL);
            pPaznic.add(new Paragraph(paznic.getNume() + " " + paznic.getPrenume(), FONT_BOLD));
            pPaznic.add(new Paragraph(", reprezentant al echipei de interventie, ca urmare a verificarii timpilor de interventie la obiectivele monitorizate.", FONT_NORMAL));
            document.add(pPaznic);
            document.add(new Paragraph("\n", FONT_NORMAL));

            // Orele
            Paragraph pOraDeclansare = new Paragraph("Alarma a fost declansata la ora ", FONT_NORMAL);
            pOraDeclansare.add(new Paragraph(TIME_FORMATTER.format(request.getOra_declansare_alarma()), FONT_BOLD));
            document.add(pOraDeclansare);
            document.add(new Paragraph("\n", FONT_NORMAL));

            Paragraph pOraPrezentare = new Paragraph("Echipajul de interventie s-a prezentat la ora ", FONT_NORMAL);
            pOraPrezentare.add(new Paragraph(TIME_FORMATTER.format(request.getOra_prezentare_echipaj()), FONT_BOLD));
            document.add(pOraPrezentare);
            document.add(new Paragraph("\n", FONT_NORMAL));

            String observatii = request.getObservatii_generale();
            if (observatii == null || observatii.trim().isEmpty()) {
                observatii = "In timpul interventiei echipajul de interventie a actionat conform procedurilor in vigoare, neexistand observatii din partea beneficiarului privind calitatea prestatiei.";
            }
            document.add(new Paragraph(observatii, FONT_NORMAL));
            document.add(new Paragraph("\n", FONT_NORMAL));

            Paragraph pOraIncheiere = new Paragraph("Misiunea s-a incheiat la ora ", FONT_NORMAL);
            pOraIncheiere.add(new Paragraph(TIME_FORMATTER.format(request.getOra_incheiere_misiune()), FONT_BOLD));
            document.add(pOraIncheiere);
            document.add(new Paragraph("\n", FONT_NORMAL));

            document.add(new Paragraph("Drept pentru care am incheiat prezentul proces-verbal in 2 (doua) exemplare.", FONT_NORMAL));
            document.add(new Paragraph("\n\n\n\n\n", FONT_NORMAL)); // Spațiu

            // Secțiunea de semnături (cu un tabel PdfPTable)
            PdfPTable semanturi = new PdfPTable(2); // 2 coloane
            semanturi.setWidthPercentage(100);

            PdfPCell cellStanga = new PdfPCell(new Paragraph("ECHIPA DE INTERVENTIE\n____________________", FONT_NORMAL));
            cellStanga.setBorder(Rectangle.NO_BORDER);

            PdfPCell cellDreapta = new PdfPCell(new Paragraph("BENEFICIAR\n____________________", FONT_NORMAL));
            cellDreapta.setBorder(Rectangle.NO_BORDER);
            cellDreapta.setHorizontalAlignment(Element.ALIGN_RIGHT);

            semanturi.addCell(cellStanga);
            semanturi.addCell(cellDreapta);
            document.add(semanturi);

            // --- PAGINA 2: Tabelul de evenimente ---
            document.newPage(); // Echivalentul lui addAreaBreak()

            float[] columnWidths = {30, 80, 70, 60, 50, 70, 80, 80};
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            // Adăugăm antetul
            addHeaderCell(table, "Nr. crt.");
            addHeaderCell(table, "Data/Ora Rec.");
            addHeaderCell(table, "Tip Alarma");
            addHeaderCell(table, "Echipaj");
            addHeaderCell(table, "Ora Sosirii");
            addHeaderCell(table, "Cauze");
            addHeaderCell(table, "Solutionare");
            addHeaderCell(table, "Observatii");

            // Adăugăm rândurile de date
            int nrCrt = 1;
            for (Eveniment ev : request.getEvenimente()) {
                table.addCell(new Paragraph(String.valueOf(nrCrt++), FONT_NORMAL));
                table.addCell(new Paragraph(DATETIME_FORMATTER.format(ev.getDataOraReceptionarii()), FONT_NORMAL));
                table.addCell(new Paragraph(ev.getTipulAlarmei(), FONT_NORMAL));
                table.addCell(new Paragraph(ev.getEchipajAlarmat(), FONT_NORMAL));
                table.addCell(new Paragraph(TIME_FORMATTER.format(ev.getOraSosirii()), FONT_NORMAL));
                table.addCell(new Paragraph(ev.getCauzeleAlarmei(), FONT_NORMAL));
                table.addCell(new Paragraph(ev.getModulDeSolutionare(), FONT_NORMAL));
                table.addCell(new Paragraph(ev.getObservatii() != null ? ev.getObservatii() : "", FONT_NORMAL));
            }

            document.add(table);

        } catch (Exception e) {
            // Aruncăm o IOException dacă ceva eșuează la scrierea PDF-ului
            throw new IOException("Eroare la generarea PDF-ului: " + e.getMessage(), e);
        } finally {
            // Trebuie să închidem documentul manual
            if (document.isOpen()) {
                document.close();
            }
        }

        // --- PASUL 4: Salvarea în baza de date (rămâne la fel) ---
        ProcesVerbal newProcesVerbal = new ProcesVerbal();
        newProcesVerbal.setPontaj(pontaj);
        newProcesVerbal.setPaznic(paznic);
        newProcesVerbal.setReprezentantBeneficiar(request.getReprezentant_beneficiar());
        newProcesVerbal.setOraDeclansareAlarma(request.getOra_declansare_alarma());
        newProcesVerbal.setOraPrezentareEchipaj(request.getOra_prezentare_echipaj());
        newProcesVerbal.setOraIncheiereMisiune(request.getOra_incheiere_misiune());
        newProcesVerbal.setObservatiiGenerale(request.getObservatii_generale());
        newProcesVerbal.setEvenimente(request.getEvenimente());
        newProcesVerbal.setCaleStocarePDF(caleStocareRelativa);

        return procesVerbalRepository.save(newProcesVerbal);
    }

    // Funcție ajutătoare pentru a stiliza celulele de antet
    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Paragraph(text, FONT_BOLD));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}
