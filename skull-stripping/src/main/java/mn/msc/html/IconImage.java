package mn.msc.html;

import java.io.IOException;

import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

/**
 * @author niemar
 *
 */
public class IconImage implements Renderable {
	 
    public HtmlAttributes attributes = new HtmlAttributes();
     
    public IconImage(String path, String width) {
        super();
        this.attributes
            .src(path)
            .width(width)
        	.alt("Image " + path + " not available !.")
        	.border("1");
    }
    
    
    public void renderOn(HtmlCanvas html) throws IOException {
        html.img(attributes);
    }              
}