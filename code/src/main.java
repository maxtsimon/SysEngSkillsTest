import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class main {

    private static void paintImage(Graphics2D g, String[] coordinates) throws IOException {
        int x = Integer.parseInt(coordinates[0]);
        int y = Integer.parseInt(coordinates[1]);
        int xOffset = Integer.parseInt(coordinates[2]);
        int yOffset = Integer.parseInt(coordinates[3]);

        g.setColor(Color.yellow);
        g.setStroke(new BasicStroke(10));
        g.drawRect(x, y, Math.abs(x - xOffset), Math.abs(y-yOffset));
    }

    private static void saveImage(BufferedImage guiPicture, String filename) throws IOException {
        ImageIO.write(guiPicture, "PNG", new File("output/" + filename));
    }

    public static void main(String[] args) {

        File folder = new File("input");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.getName().endsWith("xml")) {

                String filename = file.getName().substring(0, file.getName().length() - 4);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                try {

                    BufferedImage guiPicture = ImageIO.read(new File("input/" + filename + ".png"));
                    Graphics2D g = (Graphics2D) guiPicture.getGraphics();

                    DocumentBuilder builder = factory.newDocumentBuilder();

                    Document document = builder.parse(new File("input/" + filename + ".xml"));

                    document.getDocumentElement().normalize();

                    NodeList guiElements = document.getElementsByTagName("node");
                    for (int i = 0; i < guiElements.getLength(); i++) {
                        if (!guiElements.item(i).hasChildNodes()) {
                            Element e = (Element) guiElements.item(i);
                            String bounds = e.getAttribute("bounds").replace("[", "").replace("]", ",");
                            String[] arr = bounds.split(",");
                            paintImage(g, arr);
                        }
                    }

                    saveImage(guiPicture, filename + ".png");

                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
