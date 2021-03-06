package org.vandeseer.easytable.drawing.cell;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.vandeseer.easytable.drawing.DrawingContext;
import org.vandeseer.easytable.drawing.DrawingUtil;
import org.vandeseer.easytable.drawing.PositionedStyledText;
import org.vandeseer.easytable.structure.cell.AbstractTextCell;
import org.vandeseer.easytable.structure.cell.TextCell;
import org.vandeseer.easytable.util.PdfUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.vandeseer.easytable.settings.HorizontalAlignment.*;

@NoArgsConstructor
public class TextCellDrawer<T extends AbstractTextCell> extends AbstractCellDrawer<AbstractTextCell> {

    public TextCellDrawer(T cell) {
        this.cell = cell;
    }

    @Override
    @SneakyThrows
    public void drawContent(DrawingContext drawingContext) {
        final float startX = drawingContext.getStartingPoint().x;

        PDFont currentFont = cell.getFont();
        int currentFontSize = cell.getFontSize();
        Color currentTextColor = cell.getTextColor();

        float yOffset = drawingContext.getStartingPoint().y + getAdaptionForVerticalAlignment();
        float xOffset = startX + cell.getPaddingLeft();

        float textWidth = 0f;

        TextCell superScriptCell = null;
        if (cell instanceof TextCell) {
            TextCell textCell = (TextCell) cell;
            if (textCell.hasSuperScript()) {
                superScriptCell = textCell.getSuperScript();
            }
        }
        List<String> lines;
        if (superScriptCell != null) {
            lines = calculateAndGetLines(currentFont, currentFontSize, superScriptCell.getFont(), superScriptCell.getFontSize(), cell.getMaxWidth());
        } else {
            lines = calculateAndGetLines(currentFont, currentFontSize, cell.getMaxWidth());
        }

        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);

            yOffset -= calculateYOffset(currentFont, currentFontSize, i);

            textWidth = PdfUtil.getStringWidth(line, currentFont, currentFontSize);

            // Handle horizontal alignment by adjusting the xOffset
            if (cell.isHorizontallyAligned(RIGHT)) {
                if (superScriptCell != null){
                   float superTextWidth = PdfUtil.getStringWidth(superScriptCell.getText(), superScriptCell.getFont(), superScriptCell.getFontSize());
                    xOffset =  startX + (cell.getWidth()- (textWidth + cell.getPaddingRight())-  (superTextWidth+superScriptCell.getPaddingRight()));
                }else{
                    xOffset = startX + (cell.getWidth() - (textWidth + cell.getPaddingRight()));
                }
            } else if (cell.isHorizontallyAligned(CENTER)) {
                xOffset = startX + (cell.getWidth() - textWidth) / 2;

            } else if (cell.isHorizontallyAligned(JUSTIFY) && isNotLastLine(lines, i)) {
                drawingContext.getContentStream().setCharacterSpacing(calculateCharSpacingFor(line));
            }

            drawText(
                    drawingContext,
                    PositionedStyledText.builder()
                            .x(xOffset)
                            .y(yOffset)
                            .text(line)
                            .font(currentFont)
                            .fontSize(currentFontSize)
                            .color(currentTextColor)
                            .build()
            );
        }


        if (cell instanceof TextCell) {
            TextCell textCell = (TextCell) cell;

            if (textCell.hasSuperScript()) {
                cell = textCell.getSuperScript();
                currentFont = cell.getFont();
                currentFontSize = cell.getFontSize();
                currentTextColor = cell.getTextColor();
                xOffset = xOffset + textWidth;
                final String line = cell.getText();
                drawText(
                        drawingContext,
                        PositionedStyledText.builder()
                                .x(xOffset)
                                .y(yOffset)
                                .text(line)
                                .font(currentFont)
                                .fontSize(currentFontSize)
                                .color(currentTextColor)
                                .textRise(cell.getTextRise())
                                .build()
                );
            }
        }


    }

    @Override
    protected float calculateInnerHeight() {
        return cell.getTextHeight();
    }


    private float calculateYOffset(PDFont currentFont, int currentFontSize, int lineIndex) {
        return PdfUtil.getFontHeight(currentFont, currentFontSize) // font height
                + (lineIndex > 0 ? PdfUtil.getFontHeight(currentFont, currentFontSize) * cell.getLineSpacing() : 0f); // line spacing
    }

    static boolean isNotLastLine(List<String> lines, int i) {
        return i != lines.size() - 1;
    }

    // Code from https://stackoverflow.com/questions/20680430/is-it-possible-to-justify-text-in-pdfbox
    protected float calculateCharSpacingFor(String line) {
        float charSpacing = 0;
        if (line.length() > 1) {
            float size = PdfUtil.getStringWidth(line, cell.getFont(), cell.getFontSize());
            float free = cell.getWidthOfText() - size;
            if (free > 0) {
                charSpacing = free / (line.length() - 1);
            }
        }
        return charSpacing;
    }


    protected List<String> calculateAndGetLines(PDFont currentFont, int currentFontSize,
                                                PDFont superScriptFont, int superScriptSize,
                                                float maxWidth) {

        String superScriptText = null;
        TextCell textCell = (TextCell) cell;
        if (textCell.hasSuperScript()) {
            superScriptText = textCell.getSuperScript().getText();
        }
        return cell.isWordBreak()
                ? PdfUtil.getOptimalTextBreakLines(cell.getText(), superScriptText, currentFont, currentFontSize, superScriptFont, superScriptSize, maxWidth)
                : Collections.singletonList(cell.getText());

    }

    protected List<String> calculateAndGetLines(PDFont currentFont, int currentFontSize, float maxWidth) {


        return cell.isWordBreak()
                ? PdfUtil.getOptimalTextBreakLines(cell.getText(), currentFont, currentFontSize, maxWidth)
                : Collections.singletonList(cell.getText());
    }

    protected void drawText(DrawingContext drawingContext, PositionedStyledText positionedStyledText) throws IOException {
        DrawingUtil.drawText(
                drawingContext.getContentStream(),
                positionedStyledText
        );
    }

}
