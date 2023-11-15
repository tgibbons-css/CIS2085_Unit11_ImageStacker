/*
    Project idea, directions, and code credit:
    John Nicholson
    Austin Peay State University, Clarksville, TN
*/

package imagestacker;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ImageStacker
{
    // Takes in a two-dimensional array of ints (pixel values).
    // The first dimension represents the number of images (likely 10).
    // The second dimension represents the pixel values of each image.
    // So refer to the images as images[layer][pixel]
    public static int[] averagePixelValues(int[][] images)
    {
        int numOfLayers = images.length;        // number of image layers (likely 10)
        int numOfPixels = images[0].length;     // number of pixels (about 2,500,000)
        // Create a new array the same length as the original ones.
        int[] averages = new int[numOfPixels];
        int sum;
        
        System.out.println("number of pixels = "+numOfPixels);
        
        // For each pixel position, calculate the average value across all the 
        // images for that particular pixel (add up the pixel values and divide
        // by the number of images).  Store this value in the averages array.
        for(int pixelIndex = 0; pixelIndex < numOfPixels; pixelIndex++)
        {
            // Reset sum to 0.

            // Calculate the sum of all the pixel values at this particular pixelIndex.

            // Divide the sum by the number of images to get the average.

            // Store the result in the averages array.
            
        }
        
        // The averages array.  It should now contain the average value for each pixel.
        return averages;
    }
    
    private File sourceDirectory;
    
    public static void main(String[] args) throws IOException {
        ImageStacker converter = new ImageStacker();
        ImageStacker.Chooser chooser = converter.new Chooser();
        chooser.setVisible(true);
    }

    private void processFiles() throws IOException {
        File[] originalImages = sourceDirectory.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                String tmp = name.toLowerCase();
                return tmp.endsWith("jpg") || tmp.endsWith("gif") || tmp.endsWith("bmp") || tmp.endsWith("png");
            }
        });
        
        // Display the image on the screen
        displayImage(originalImages[0], "One of the orignial images");
        
        BufferedImage images[] = new BufferedImage[originalImages.length];
        
        for(int index = 0; index < originalImages.length; index++)
        {
            images[index] = ImageIO.read(originalImages[index]);
        }
        
        int[][] rgbImages = new int[images.length][];
        
        for(int index = 0; index < originalImages.length; index++)
        {
            rgbImages[index] = createRgbArray(images[index]);
        }
        
        int[] newImageArray = averagePixelValues(rgbImages);
        
        // Create new BufferedImage for final result.  Start with first image as a starting point.
        BufferedImage newImage = ImageIO.read(originalImages[0]);
        
        // Convert RGB array into a 2D array of Colors.
        Color[][] colorArray = createColorArray(newImageArray, newImage.getHeight(), newImage.getWidth());
        
        // Update new image with final pixel values.
        updatePixels(newImage, colorArray);
        
        // Write the new file.
        String fileName = originalImages[0].getName();
        fileName = fileName.substring(0, fileName.length() - 8);
        
        String destinationFileName = fileName + "_final.png";
        File destinationFile = new File(sourceDirectory, destinationFileName);
        ImageIO.write(newImage, "PNG", destinationFile);

        //JOptionPane.showMessageDialog(null, "Done.");
                
        // Display the image on the screen
        displayImage(destinationFile, "New updated image");
    }
    
    public static void updatePixels(BufferedImage image, Color[][] colorArray)
    {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, colorArray[y][x].getRGB());
            }
        }
    }
    
    public static Color[][] createColorArray(int[] rgbArray, int height, int width)
    {
        Color[][] colorArray = new Color[height][width];
        int index = 0;
        int red;
        int green;
        int blue;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                red = rgbArray[index];
                index++;
                green = rgbArray[index];
                index++;
                blue = rgbArray[index];
                index++;
                
                colorArray[y][x] = new Color(red, green, blue);
            }
        }
        
        return colorArray;
    }
    
    public static int[] createRgbArray(BufferedImage image)
    {
        int height = image.getHeight();
        int width = image.getWidth();
        
        Color pixel;
        int[] rgbArray = new int[height * width * 3];
        int index = 0;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixel = new Color(image.getRGB(x, y));
                
                rgbArray[index] = pixel.getRed();
                index++;
                
                rgbArray[index] = pixel.getGreen();
                index++;
                
                rgbArray[index] = pixel.getBlue();
                index++;
            }
        }
        
        return rgbArray;
    }

    class Chooser extends JFrame implements ActionListener {
        private static final long serialVersionUID = -8389660714995462075L;

        private JTextField sourceDirTF;
        private JTextField destDirTF;
        private JButton sourceButton;
        private JButton destButton;

        public Chooser() throws IOException {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();

            // source
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.gridx = 0;
            constraints.gridy = 0;
            panel.add(new JLabel("Source: "), constraints);

            sourceDirectory = new File(".");
            sourceDirTF = new JTextField();
            sourceDirTF.setText(sourceDirectory.getCanonicalPath());
            constraints.gridx++;
            panel.add(sourceDirTF, constraints);

            sourceButton = new JButton("Browse");
            sourceButton.addActionListener(this);
            constraints.gridx++;
            panel.add(sourceButton, constraints);

            // destination
            constraints.gridx = 0;
            constraints.gridy++;
            panel.add(new JLabel("Destination: "), constraints);

            // process
            JButton processButton = new JButton("Process");
            processButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    try {
                        processFiles();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            constraints.gridx = 0;
            constraints.gridy++;
            panel.add(processButton, constraints);

            add(panel);

            pack();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationByPlatform(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            File current;
            String title;
            current = sourceDirectory;
            title = "Choose source folder";

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(title);
            chooser.setCurrentDirectory(current);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File dir = chooser.getSelectedFile();
                try {
                    sourceDirectory = dir;
                    sourceDirTF.setText(sourceDirectory.getCanonicalPath());
                } catch (IOException ex) {}
            }
        }
    }
    
        /**
     * Display an image in a dialog popup window with a given title
     *
     * @param imgFile
     * @param title
     */
    public static void displayImage(File imgFile, String title) {
        BufferedImage image;
        try {
            image = ImageIO.read(imgFile);
            JLabel picLabel = new JLabel(new ImageIcon(image));
            JOptionPane.showMessageDialog(null, picLabel, title, JOptionPane.PLAIN_MESSAGE, null);
        } catch (IOException ex) {
            System.out.println("Image file not found at " + imgFile.getAbsolutePath());
        }

    }
}
