package com.securitate.agentie.backend.service;

import com.securitate.agentie.backend.dto.RaportEvenimentRequest;
import com.securitate.agentie.backend.model.RaportEveniment;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.repository.PontajRepository;
import com.securitate.agentie.backend.repository.RaportEvenimentRepository;
import com.securitate.agentie.backend.repository.UserRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RaportEvenimentService {

    private final RaportEvenimentRepository raportEvenimentRepository;
    private final UserRepository userRepository;
    private final PontajRepository pontajRepository;

    private static final Font FONT_NORMAL = new Font(Font.HELVETICA, 10, Font.NORMAL);
    private static final Font FONT_BOLD = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final Path pdfRootDir = Paths.get("uploads/rapoarte-evenimente");

    public RaportEvenimentService(
            RaportEvenimentRepository raportEvenimentRepository,
            UserRepository userRepository,
            PontajRepository pontajRepository
    ) {
        this.raportEvenimentRepository = raportEvenimentRepository;
        this.userRepository = userRepository;
        this.pontajRepository = pontajRepository;

        try {
            Files.createDirectories(this.pdfRootDir);
        } catch (IOException e) {
            System.err.println("--- NU S-A PUTUT CREA DIRECTORUL PENTRU PDF-uri Rapoarte Evenimente ---");
            e.printStackTrace();
        }
    }

    /**
     * ✅ Returnează DTO (nu entitatea JPA) ca să evităm LazyInitializationException la serializare JSON.
     */
    public RaportEvenimentResponse createRaportEveniment(RaportEvenimentRequest request, User paznicLogat) throws IOException {
        if (paznicLogat == null) {
            throw new IllegalStateException("Utilizator neautentificat.");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request invalid (null).");
        }
        if (request.getBeneficiaryId() == null) {
            throw new IllegalArgumentException("BeneficiaryId este obligatoriu.");
        }

        // Beneficiar
        User beneficiary = userRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new IllegalArgumentException("Beneficiarul nu a fost găsit."));

        // Tură activă
        if (pontajRepository.findByPaznicAndOraIesireIsNull(paznicLogat).isEmpty()) {
            throw new IllegalStateException("Nu aveți o tură activă pentru a genera documente.");
        }

        // Validări request
        requireNotBlank(request.getPunctDeLucru(), "Câmpul 'La postul Nr.' este obligatoriu.");
        if (request.getDataRaport() == null) {
            throw new IllegalArgumentException("Câmpul 'Data raport' este obligatoriu.");
        }
        if (request.getDataConstatare() == null) {
            throw new IllegalArgumentException("Câmpul 'Data constatăre' este obligatoriu.");
        }
        requireNotBlank(request.getFunctiePaznic(), "Câmpul 'Funcția' este obligatoriu.");
        requireNotBlank(request.getOraConstatare(), "Câmpul 'Ora constatării' este obligatoriu.");
        requireNotBlank(request.getDescriereFapta(), "Câmpul 'Descrierea faptei/evenimentului' este obligatoriu.");
        requireNotBlank(request.getCazSesizatLa(), "Câmpul 'Caz sesizat la' este obligatoriu.");

        // Societate (obligatoriu în DB)
        if (beneficiary.getProfile() == null) {
            throw new IllegalStateException("Beneficiarul nu are profil completat (profile lipsă).");
        }
        String societate = beneficiary.getProfile().getNumeFirma();
        requireNotBlank(societate, "Beneficiarul nu are completat 'Nume firmă' în profil.");

        // Convert LocalDate -> LocalDateTime (entity are LocalDateTime)
        LocalDateTime dataRaportLdt = request.getDataRaport().atStartOfDay();
        LocalDateTime dataConstatareLdt = request.getDataConstatare().atStartOfDay();

        // --- Generare PDF ---
        String fileName = "RE_" + paznicLogat.getId() + "_" + System.currentTimeMillis() + ".pdf";
        Path filePath = this.pdfRootDir.resolve(fileName);
        String caleStocareRelativa = "/uploads/rapoarte-evenimente/" + fileName;

        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toString()));
            document.open();

            Paragraph titlu = new Paragraph(
                    "RAPORT DE EVENIMENT",
                    new Font(Font.HELVETICA, 14, Font.BOLD, Color.RED)
            );
            titlu.setAlignment(Element.ALIGN_CENTER);
            document.add(titlu);
            document.add(new Paragraph("\n"));

            Paragraph p1 = new Paragraph(
                    "Nr. Raport: " + (request.getNumarRaport() != null ? request.getNumarRaport() : "Nespecificat")
                            + ", Data: " + DATE_FORMATTER.format(request.getDataRaport()),
                    FONT_NORMAL
            );
            p1.setAlignment(Element.ALIGN_CENTER);
            document.add(p1);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Subsemnatul/a: " + paznicLogat.getNume() + " " + paznicLogat.getPrenume(), FONT_NORMAL));
            document.add(new Paragraph("Funcția: " + request.getFunctiePaznic().trim(), FONT_NORMAL));
            document.add(new Paragraph("Societatea: " + societate.trim(), FONT_NORMAL));
            document.add(new Paragraph("La postul Nr.: " + request.getPunctDeLucru().trim(), FONT_NORMAL));

            document.add(new Paragraph(
                    "Data și ora constatării: " + DATE_FORMATTER.format(request.getDataConstatare())
                            + " ora " + request.getOraConstatare().trim(),
                    FONT_NORMAL
            ));

            String faptuitor = request.getNumeFaptuitor() != null ? request.getNumeFaptuitor() : "";
            document.add(new Paragraph("Numele făptuitorului (dacă este cazul): " + faptuitor, FONT_NORMAL));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Descrierea faptei/evenimentului:", FONT_BOLD));
            document.add(new Paragraph(request.getDescriereFapta(), FONT_NORMAL));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Caz sesizat la (Poliție/Pompieri/etc.): " + request.getCazSesizatLa(), FONT_NORMAL));
            document.add(new Paragraph("\n\n"));

            Paragraph semnatura = new Paragraph("Semnătura: ______________________", FONT_NORMAL);
            semnatura.setAlignment(Element.ALIGN_LEFT);
            document.add(semnatura);

        } catch (Exception e) {
            throw new IOException("Eroare la generarea PDF-ului Raport Eveniment: " + e.getMessage(), e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        // --- Salvare în DB ---
        RaportEveniment newRaport = new RaportEveniment();
        newRaport.setPaznic(paznicLogat);
        newRaport.setBeneficiary(beneficiary);

        newRaport.setPunctDeLucru(request.getPunctDeLucru().trim());
        newRaport.setNumarRaport(request.getNumarRaport());

        newRaport.setDataRaport(dataRaportLdt);
        newRaport.setDataConstatare(dataConstatareLdt);

        newRaport.setNumePaznic(paznicLogat.getNume() + " " + paznicLogat.getPrenume());
        newRaport.setFunctiePaznic(request.getFunctiePaznic().trim());
        newRaport.setSocietate(societate.trim());
        newRaport.setOraConstatare(request.getOraConstatare().trim());
        newRaport.setNumeFaptuitor(request.getNumeFaptuitor());
        newRaport.setDescriereFapta(request.getDescriereFapta());
        newRaport.setCazSesizatLa(request.getCazSesizatLa());
        newRaport.setCaleStocarePDF(caleStocareRelativa);

        RaportEveniment saved = raportEvenimentRepository.save(newRaport);

        // ✅ DTO response (evită serializarea entităților lazy)
        return new RaportEvenimentResponse(
                saved.getId(),
                saved.getNumarRaport(),
                saved.getPunctDeLucru(),
                saved.getCaleStocarePDF(),
                saved.getCreatedAt()
        );
    }

    /**
     * ⚠️ Dacă folosești și acest endpoint, recomand să nu returnezi entități direct,
     * ci tot DTO-uri (altfel vei avea aceeași problemă cu LAZY).
     *
     * Pentru moment, îl las, dar ideal creezi un DTO list.
     */
    public List<RaportEveniment> getDocumente() {
        return raportEvenimentRepository.findAll();
    }

    private static void requireNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * DTO simplu pentru răspuns (fără relații JPA).
     * Dacă preferi, îl mutăm într-un fișier separat în package dto.
     */
    public static class RaportEvenimentResponse {
        private final Long id;
        private final String numarRaport;
        private final String punctDeLucru;
        private final String caleStocarePDF;
        private final LocalDateTime createdAt;

        public RaportEvenimentResponse(Long id, String numarRaport, String punctDeLucru, String caleStocarePDF, LocalDateTime createdAt) {
            this.id = id;
            this.numarRaport = numarRaport;
            this.punctDeLucru = punctDeLucru;
            this.caleStocarePDF = caleStocarePDF;
            this.createdAt = createdAt;
        }

        public Long getId() { return id; }
        public String getNumarRaport() { return numarRaport; }
        public String getPunctDeLucru() { return punctDeLucru; }
        public String getCaleStocarePDF() { return caleStocarePDF; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
}
