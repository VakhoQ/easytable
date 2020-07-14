package org.vandeseer.easytable.drawing;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.vandeseer.easytable.settings.BorderStyle;

import java.awt.*;
import java.io.IOException;

import static org.vandeseer.easytable.settings.BorderStyle.SOLID;

public class DrawingUtil {

    private DrawingUtil() {
    }

    public static void drawText(PDPageContentStream contentStream, PositionedStyledText styledText) throws IOException {
        contentStream.beginText();
        contentStream.setTextRise(styledText.getTextRise());
        contentStream.setNonStrokingColor(styledText.getColor());
        contentStream.setFont(styledText.getFont(), styledText.getFontSize());
        contentStream.newLineAtOffset(styledText.getX(), styledText.getY());
        contentStream.showText(styledText.getText());
        contentStream.endText();
        contentStream.setCharacterSpacing(0);
    }

    public static void drawLine(PDPageContentStream contentStream, PositionedLine line) throws IOException {

        if(line.getBorderStyle() !=null && line.getBorderStyle() == BorderStyle.DOTTED){
            contentStream.setLineDashPattern (new float[]{1}, 1);
        }else{
            contentStream.setLineDashPattern (new float[]{}, 0);
        }


        contentStream.moveTo(line.getStartX(), line.getStartY());
        contentStream.setLineWidth(line.getWidth());
        contentStream.lineTo(line.getEndX(), line.getEndY());
        contentStream.setStrokingColor(line.getColor());
        contentStream.setLineDashPattern(line.getBorderStyle().getPattern(), line.getBorderStyle().getPhase());
        contentStream.stroke();
        contentStream.setStrokingColor(line.getResetColor());
        contentStream.setLineDashPattern(SOLID.getPattern(), SOLID.getPhase());
    }

    public static void drawRectangle(PDPageContentStream contentStream, PositionedRectangle rectangle)
            throws IOException {
        contentStream.setNonStrokingColor(rectangle.getColor());

        contentStream.addRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        contentStream.fill();

        // Reset NonStrokingColor to default value
        contentStream.setNonStrokingColor(Color.BLACK);
    }
}
