package be.quodlibet.dndgenerate;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.html.HtmlRenderer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MarkdownToHtmlConverter {
    public static String convertMarkdownToHtml(String markdown) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
    public static String convertHtmlToPdf(String html, String outputPath, String title) {
        String filename =  UniqueFilenameGenerator.getUniqueFilename(outputPath, title +".pdf");
        try (OutputStream os = new FileOutputStream(outputPath +File.separator  + filename )) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputPath +File.separator + filename;
    }
    public static String enhanceHtmlWithImagesAndStyles(String html) {
    // Example: Insert an image before a specific text
    html = html.replace("A piece of text", "<img src='path/to/image.png' />A piece of text");

    // Add styles for headings, subheadings, etc.
    html = html.replace("<h1>", "<h1 style='font-size:24px;'>");
    html = html.replace("<h2>", "<h2 style='font-size:20px;'>");

    return html;
}
    

}
