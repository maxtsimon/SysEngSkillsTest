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

    // Given a set of bounds for a GUI element, render a rectangle with a given graphics context for a buffered image
    private static void paintImage(Graphics2D g, String[] coordinates) throws IOException {
        int x = Integer.parseInt(coordinates[0]);
        int y = Integer.parseInt(coordinates[1]);
        int xOffset = Integer.parseInt(coordinates[2]);
        int yOffset = Integer.parseInt(coordinates[3]);

        g.setColor(Color.yellow);
        g.setStroke(new BasicStroke(10));
        g.drawRect(x, y, Math.abs(x - xOffset), Math.abs(y-yOffset));
    }

    // Save buffered image to output folder
    private static void saveImage(BufferedImage guiPicture, String filename, String outputDirectory) throws IOException {
        ImageIO.write(guiPicture, "PNG", new File(outputDirectory + "/" + filename));
    }

    // args[0] = absolute path of input folder
    // args[1] = absolute path of output folder
    public static void main(String[] args) {

        String inputDir = args[0];
        String outputDir = args[1];

        File folder = new File(inputDir);
        File[] listOfFiles = folder.listFiles();

        // for each pair of files in the input folder

        if (listOfFiles != null) {
            for (File file : listOfFiles) {

                // only create one output image per xml/png pair by processing xml files only

                if (file.getName().endsWith("xml")) {

                    String filename = file.getName().substring(0, file.getName().length() - 4);

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                    try {

                        // make buffered image out of png input and create associated graphics context to render on

                        BufferedImage guiPicture = ImageIO.read(new File(inputDir + "/" + filename + ".png"));
                        Graphics2D g = (Graphics2D) guiPicture.getGraphics();

                        DocumentBuilder builder = factory.newDocumentBuilder();

                        // parse xml input and create list of components tagged with "node"

                        Document document = builder.parse(new File(inputDir + "/" + filename + ".xml"));

                        document.getDocumentElement().normalize();

                        NodeList nodes = document.getElementsByTagName("node");

                        // for each node in the list, if the node does not have any children, it is a gui element
                            // get the bounds of that node and perform string manipulation to cast the bounds info as an array
                            // send that array to the paintImage function where a rectangle will be drawn given the bounds

                        for (int i = 0; i < nodes.getLength(); i++) {
                            if (!nodes.item(i).hasChildNodes()) {
                                Element e = (Element) nodes.item(i);
                                String bounds = e.getAttribute("bounds").replace("[", "").replace("]", ",");
                                String[] arr = bounds.split(",");
                                paintImage(g, arr);
                            }
                        }

                        // save the image after the entire xml is parsed and all rectangles are rendered on the graphics context

                        saveImage(guiPicture, filename + ".png", outputDir);

                    } catch (ParserConfigurationException | SAXException | IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
