package org.vandeseer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.awt.*;
import java.io.IOException;

public class MinimumWorkingExample {

    public static void main(String[] args) throws IOException {

        try (PDDocument document = new PDDocument()) {
            final PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // Build the table
                Table myTable = Table.builder()
                        .addColumnsOfWidth(200, 200)
                        .padding(2)
                        .addRow(Row.builder()
                                .add(TextCell.builder().text("One One").borderWidth(4).backgroundColor(Color.WHITE).build())
                                .add(TextCell.builder().text("One Two").borderWidth(0).backgroundColor(Color.YELLOW).build())
                                .build())
                        .addRow(Row.builder()
                                .padding(10)
                                .add(TextCell.builder().text("Two One").textColor(Color.RED).build())
                                .add(TextCell.builder().text("Two Two").horizontalAlignment(HorizontalAlignment.RIGHT).build())
                                .build())
                        .addRow(Row.builder()
                                .padding(10)
                                .add(TextCell.builder().text("The last word is to the same line").textColor(Color.RED)
                                        .superScript(TextCell.builder().text("1").fontSize(6).textRise(5).build())
                                        .build())
                                .add(TextCell.builder()
                                        .text("The last word ======> to next line").borderWidth(4).backgroundColor(Color.WHITE)
                                        .superScript(TextCell.builder().text("2").fontSize(6).textRise(5).build())
                                        .build())
                                .build())
                        .build();

                // Set up the drawer
                TableDrawer tableDrawer = TableDrawer.builder()
                        .contentStream(contentStream)
                        .startX(20f)
                        .startY(page.getMediaBox().getUpperRightY() - 20f)
                        .table(myTable)
                        .build();

                // And go for it!
                tableDrawer.draw();
            }

            document.save("example.pdf");
        }
    }

}
