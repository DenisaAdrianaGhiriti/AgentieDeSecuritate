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
import org.springframework.transaction.annotation.Transactional;

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

    // --- Fonturi OpenPDF ---
    private static final Font FONT_NORMAL = new Font(Font.HELVETICA, 10, Font.NORMAL);
    private static final Font FONT_BOLD = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    // ------------------------

    private final Path pdfRootDir = Paths.get("uploads/rapoarte-evenimente");

    public RaportEvenimentService(RaportEvenimentRepository raportEvenimentRepository, UserRepository userRepository, PontajRepository pontajRepository) {
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

    public RaportEveniment createRaportEveniment(RaportEvenimentRequest request, User paznicLogat) throws IOException {
        User beneficiary = userRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new IllegalArgumentException("Beneficiarul nu a fost găsit."));

        if (pontajRepository.findByPaznicAndOraIesireIsNull(paznicLogat).isEmpty()) {
            throw new IllegalStateException("Nu aveți o tură activă pentru a genera documente.");
        }

        if (request.getPunctDeLucru() == null || request.getPunctDeLucru().trim().isEmpty()) {
            throw new IllegalArgumentException("Câmpul 'La postul Nr.' este obligatoriu.");
        }

        // --- Generare PDF (Simplificat) ---
        String fileName = "RE_" + paznicLogat.getId() + "_" + System.currentTimeMillis() + ".pdf";
        Path filePath = this.pdfRootDir.resolve(fileName);
        String caleStocareRelativa = "/uploads/rapoarte-evenimente/" + fileName;

        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toString()));
            document.open();

            // Titlu
            Paragraph titlu = new Paragraph("RAPORT DE EVENIMENT", new Font(Font.HELVETICA, 14, Font.BOLD, Color.RED));
            titlu.setAlignment(Element.ALIGN_CENTER);
            document.add(titlu);
            document.add(new Paragraph("\n"));

            // Numar/Data
            Paragraph p1 = new Paragraph("Nr. Raport: " + (request.getNumarRaport() != null ? request.getNumarRaport() : "Nespecificat")
                    + ", Data: " + DATE_FORMATTER.format(request.getDataRaport()), FONT_NORMAL);
            p1.setAlignment(Element.ALIGN_CENTER);
            document.add(p1);
            document.add(new Paragraph("\n"));

            // Paznic
            document.add(new Paragraph("Subsemnatul/a: " + paznicLogat.getNume() + " " + paznicLogat.getPrenume(), FONT_NORMAL));
            document.add(new Paragraph("Funcția: " + request.getFunctiePaznic(), FONT_NORMAL));
            document.add(new Paragraph("Societatea: " + beneficiary.getProfile().getNumeFirma(), FONT_NORMAL));
            document.add(new Paragraph("La postul Nr.: " + request.getPunctDeLucru(), FONT_NORMAL));
            document.add(new Paragraph("Data și ora constatării: " + DATE_FORMATTER.format(request.getDataConstatare()) + " ora " + request.getOraConstatare(), FONT_NORMAL));
            document.add(new Paragraph("Numele faptuitorului (dacă este cazul): " + request.getNumeFaptuitor(), FONT_NORMAL));
            document.add(new Paragraph("\n"));

            // Descriere
            document.add(new Paragraph("Descrierea faptei/evenimentului:", FONT_BOLD));
            document.add(new Paragraph(request.getDescriereFapta(), FONT_NORMAL));
            document.add(new Paragraph("\n"));

            // Masuri
            document.add(new Paragraph("Caz sesizat la (Poliție/Pompieri/etc.): " + request.getCazSesizatLa(), FONT_NORMAL));
            document.add(new Paragraph("\n\n"));

            // Semnătură
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
        // --- End Generare PDF ---

        // Salvare în baza de date
        RaportEveniment newRaport = new RaportEveniment();
        newRaport.setPaznic(paznicLogat);
        newRaport.setBeneficiary(beneficiary);
        newRaport.setPunctDeLucru(request.getPunctDeLucru());
        newRaport.setNumarRaport(request.getNumarRaport());
        newRaport.setDataRaport(request.getDataRaport());
        newRaport.setNumePaznic(paznicLogat.getNume() + " " + paznicLogat.getPrenume());
        newRaport.setFunctiePaznic(request.getFunctiePaznic());
        newRaport.setSocietate(beneficiary.getProfile().getNumeFirma());
        newRaport.setDataConstatare(request.getDataConstatare());
        newRaport.setOraConstatare(request.getOraConstatare());
        newRaport.setNumeFaptuitor(request.getNumeFaptuitor());
        newRaport.setDescriereFapta(request.getDescriereFapta());
        newRaport.setCazSesizatLa(request.getCazSesizatLa());
        newRaport.setCaleStocarePDF(caleStocareRelativa);
        // dataExpirare este setată automat în @PrePersist

        return raportEvenimentRepository.save(newRaport);
    }


    // Păstrează getDocumente() pentru listing, dacă este necesar
    public List<RaportEveniment> getDocumente() {
        return raportEvenimentRepository.findAll();
    }

}