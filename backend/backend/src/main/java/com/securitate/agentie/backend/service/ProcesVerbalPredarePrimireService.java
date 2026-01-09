package com.securitate.agentie.backend.service;

import com.lowagie.text.pdf.PdfPTable;
import com.securitate.agentie.backend.dto.ProcesVerbalPredarePrimireRequest;
import com.securitate.agentie.backend.model.Pontaj;
import com.securitate.agentie.backend.model.ProcesVerbalPredarePrimire;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.repository.PontajRepository;
import com.securitate.agentie.backend.repository.ProcesVerbalPredarePrimireRepository;
import com.securitate.agentie.backend.repository.UserRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Rectangle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.securitate.agentie.backend.dto.ProcesVerbalPredarePrimireListItem;

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
public class ProcesVerbalPredarePrimireService {

    private final PontajRepository pontajRepository;
    private final ProcesVerbalPredarePrimireRepository pvprRepository;
    private final UserRepository userRepository;

    // --- Fonturi OpenPDF ---
    private static final Font FONT_NORMAL = new Font(Font.HELVETICA, 10, Font.NORMAL);
    private static final Font FONT_BOLD = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    // ------------------------

    private final Path pdfRootDir = Paths.get("uploads/procese-predare-primire");

    public ProcesVerbalPredarePrimireService(PontajRepository pontajRepository, ProcesVerbalPredarePrimireRepository pvprRepository, UserRepository userRepository) {
        this.pontajRepository = pontajRepository;
        this.pvprRepository = pvprRepository;
        this.userRepository = userRepository;

        try {
            Files.createDirectories(this.pdfRootDir);
        } catch (IOException e) {
            System.err.println("--- NU S-A PUTUT CREA DIRECTORUL PENTRU PDF-uri PVPP ---");
            e.printStackTrace();
        }
    }

    public ProcesVerbalPredarePrimire createProcesVerbalPredarePrimire(ProcesVerbalPredarePrimireRequest request, User paznicLogat) throws IOException {
        Pontaj pontaj = pontajRepository.findById(request.getPontajId())
                .orElseThrow(() -> new IllegalArgumentException("Pontajul asociat nu a fost găsit."));

//        if (pvprRepository.findByPontaj(pontaj).isPresent()) {
//            throw new IllegalArgumentException("Procesul verbal pentru această tură a fost deja creat.");
//        }

        User reprezentantVigilent = userRepository.findById(request.getReprezentantVigilentId())
                .orElseThrow(() -> new IllegalArgumentException("Reprezentantul Vigilent nu a fost găsit."));

        // --- Generare PDF (Simplificat) ---
        String fileName = "PVPP_" + request.getPontajId() + "_" + System.currentTimeMillis() + ".pdf";
        Path filePath = this.pdfRootDir.resolve(fileName);
        String caleStocareRelativa = "/uploads/procese-predare-primire/" + fileName;

        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toString()));
            document.open();

            // Titlu
            Paragraph titlu = new Paragraph("PROCES VERBAL DE PREDARE PRIMIRE", new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLUE));
            titlu.setAlignment(Element.ALIGN_CENTER);
            document.add(titlu);
            document.add(new Paragraph("\n\n"));

            // Text
            String text = String.format(
                    "Încheiat astăzi, %s, de către dl./d-na %s în calitate de paznic, din partea %s, " +
                            "și dl./d-na %s, reprezentant al societății care primește paza, " +
                            "cu privire la predarea primirea obiectelor și bunurilor de inventar.",
                    DATETIME_FORMATTER.format(request.getDataIncheierii()),
                    paznicLogat.getNume() + " " + paznicLogat.getPrenume(),
                    request.getReprezentantBeneficiar(), // Numele societății (beneficiarului)
                    request.getNumeReprezentantPrimire()
            );
            document.add(new Paragraph(text, FONT_NORMAL));
            document.add(new Paragraph("\n\n"));

            document.add(new Paragraph("Obiecte predate/primite:", FONT_BOLD));
            document.add(new Paragraph(request.getObiectePredate(), FONT_NORMAL));
            document.add(new Paragraph("\n\n\n"));

            // Semnături (Tabel simplu)
            PdfPTable semanturi = new PdfPTable(2);
            semanturi.setWidthPercentage(100);
            semanturi.addCell(createSignatureCell("Am predat:\n" + paznicLogat.getNume() + " " + paznicLogat.getPrenume()));
            semanturi.addCell(createSignatureCell("Am primit:\n" + request.getNumeReprezentantPrimire()));
            document.add(semanturi);


        } catch (Exception e) {
            throw new IOException("Eroare la generarea PDF-ului PVPP: " + e.getMessage(), e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
        // --- End Generare PDF ---

        // Salvare în baza de date
        ProcesVerbalPredarePrimire newPvpr = new ProcesVerbalPredarePrimire();
        newPvpr.setPontaj(pontaj);
        newPvpr.setPaznicPredare(paznicLogat);
        newPvpr.setDataIncheierii(request.getDataIncheierii());
        newPvpr.setNumeReprezentantPrimire(request.getNumeReprezentantPrimire());
        newPvpr.setObiectePredate(request.getObiectePredate());
        newPvpr.setCaleStocarePDF(caleStocareRelativa);
        newPvpr.setReprezentantBeneficiar(request.getReprezentantBeneficiar());
        newPvpr.setReprezentantVigilent(reprezentantVigilent);
        // dataExpirare este setată automat în @PrePersist

        return pvprRepository.save(newPvpr);
    }

    private PdfPCell createSignatureCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, FONT_BOLD));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(10);
        return cell;
    }

//    @Transactional
//    public int deleteExpirate() {+
//        List<ProcesVerbalPredarePrimire> expirate = pvprRepository.findByDataExpirareBefore(LocalDateTime.now());
//        pvprRepository.deleteAll(expirate);
//        return expirate.size();
//    }

    // Păstrează getDocumente() pentru listing, dacă este necesar
    public List<ProcesVerbalPredarePrimireListItem> getDocumente() {
        return pvprRepository.findAll().stream()
                .map(pv -> new ProcesVerbalPredarePrimireListItem(
                        pv.getId(),
                        pv.getCaleStocarePDF(),
                        pv.getCreatedAt(),
                        pv.getDataIncheierii(),
                        pv.getReprezentantBeneficiar(),
                        pv.getNumeReprezentantPrimire()
                ))
                .toList();
    }


}