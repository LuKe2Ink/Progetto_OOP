package utilities;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

/**
 * Class for personalized fonts.
 *
 * @author Francesco Padovani
 * @author Luigi Incarnato
 * @author Leroy Fabbri
 * @author Matteo Vanni
 *
 * @see java.awt.Font
 *
 */
public class CustomFontUtil {

  private Font customFont;

  /**
   * Constructor for fonts decision.
   *
   * @param bold control for bold or plain font
   * @param size font size
   */
  public CustomFontUtil(final boolean bold, final int size) {
    if (bold) {
      this.setCustomFontBold(size);
    } else {
      this.setCustomFont(size);
    }
  }

  /**
   * get the customized font.
   *
   * @return the custom font setted
   */
  public Font getCustomFont() {
    return customFont;
  }

  /**
   * Set bold custom font.
   *
   * @param fontSize the size of the words
   */
  private void setCustomFontBold(final int fontSize) {
    final ResourceLoader resource = new ResourceLoader();
    try {
      customFont = Font.createFont(Font.TRUETYPE_FONT, 
          resource.getStreamFont("pkmndpb")).deriveFont(Font.PLAIN,
          fontSize);
    } catch (FontFormatException | IOException e) {
      e.printStackTrace();
    }
    // new Font("data/fonts/pkmndpb.ttf", Font.PLAIN, fontSize);
  }

  /**
   * Set plain custom font.
   *
   * @param fontSize the size of the words
   */
  private void setCustomFont(final int fontSize) {
    try {
      customFont = Font.createFont(Font.TRUETYPE_FONT,
          new File("data/fonts/pkmndp.ttf")).deriveFont(Font.PLAIN,
          fontSize);
    } catch (FontFormatException | IOException e) {
      e.printStackTrace();
    }
    // new Font("data/fonts/pkmndp.ttf", Font.PLAIN, fontSize);
  }

}