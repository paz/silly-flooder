import org.json.JSONObject;
import org.json.XML;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class cum {
    public static void main(String[] args) throws Exception {
        System.out.println("Retard");
        int windowAmount = 30;
        List<ImageIcon> images = new ArrayList<>();
        String resp = getHTML("https://rule34.xxx/index.php?page=dapi&s=post&q=index&tags=femboy&limit=" + windowAmount);
        JSONObject respJson = XML.toJSONObject(resp);
        for (int i = 0; i < windowAmount; i++) {
            String furl = new JSONObject(respJson.getJSONObject("posts").getJSONArray("post").get(i).toString()).getString("file_url");
            System.out.print("Downloading " + furl + " (" + (i + 1) + "/" + windowAmount + ") ... ");
            images.add(new ImageIcon(new URL(furl)));
            System.out.println("DONE");
        }
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Random rnd = new Random();
        List<JFrame> windows = new ArrayList<>();
        for (int i = 0; i < windowAmount; i++) {
            int index = (int) Math.floor(Math.random() * images.size());
            ImageIcon current = images.get(index);
            JFrame f = displayImageInWindow(current);
            f.setLocation(rnd.nextInt(d.width), rnd.nextInt(d.height));
            windows.add(f);
        }
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                    for (JFrame window : windows) {
                        if (!window.isVisible()) continue;
                        window.setLocation(rnd.nextInt(d.width), rnd.nextInt(d.height));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static JFrame displayImageInWindow(ImageIcon img) {
        img.setImage(getScaledImage(img.getImage()));
        JFrame f = new JFrame("0");
        JLabel image = new JLabel(img);
        image.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
        f.setLocationRelativeTo(null);
        f.setUndecorated(true);
        f.setSize(img.getIconWidth(), img.getIconHeight());
        f.add(image);
        f.setVisible(true);
        f.setAlwaysOnTop(true);
        return f;
    }

    private static Image getScaledImage(Image srcImg) {
        BufferedImage resizedImg = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, 300, 300, null);
        g2.dispose();

        return resizedImg;
    }

    public static String getHTML(String urlToRead) throws Exception {
        List<String> ham = new ArrayList<>();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                ham.add(line);
            }
        }
        return String.join("\n", ham);
    }
}

