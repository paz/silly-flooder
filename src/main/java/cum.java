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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class cum {
    public static void main(String[] args) throws Exception {
        int winAmount = 30;
        int dlThreadAmount = 15;
        int windowMoveDelayMS = 1;
        System.out.println("Retard");
        ExecutorService excv = Executors.newFixedThreadPool(dlThreadAmount);
        List<ImageIcon> images = new ArrayList<>();
        String resp = getHTML("https://rule34.xxx/index.php?page=dapi&s=post&q=index&tags=femboy&limit=" + winAmount);
        JSONObject respJson = XML.toJSONObject(resp);
        for (int i = 0; i < winAmount; i++) {
            int finalI = i;
            Runnable shit = () -> {
                try {
                    String furl = new JSONObject(respJson.getJSONObject("posts").getJSONArray("post").get(finalI).toString()).getString("file_url");
                    System.out.println("Downloading " + furl + " (" + (finalI + 1) + "/" + winAmount + ") ... ");
                    images.add(new ImageIcon(new URL(furl)));
                    System.out.println("Downloaded " + furl);
                } catch (Exception ignored) {
                }
            };
            excv.execute(shit);
        }
        excv.shutdown();
        excv.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Random rnd = new Random();
        List<JFrame> windows = new ArrayList<>();
        for (int i = 0; i < winAmount; i++) {
            ImageIcon current = images.get(i);
            JFrame f = displayImageInWindow(current);
            f.setLocation(rnd.nextInt(d.width), rnd.nextInt(d.height));
            windows.add(f);
        }
        new Thread(() -> {
            while (true) {
                try {
                    boolean movedAnything = false;
                    for (JFrame window : windows) {
                        if (!window.isVisible()) continue;
                        Thread.sleep(windowMoveDelayMS);
                        Point winl = window.getLocation();
                        int nx = rnd.nextInt(7)-3+winl.x;
                        int ny = rnd.nextInt(7)-3+winl.y;
                        nx = Math.min(nx, d.width);
                        nx = Math.max(nx, 0);
                        ny = Math.min(ny, d.height);
                        ny = Math.max(ny, 0);
                        window.setLocation(nx, ny);
                        movedAnything = true;
                    }
                    if (!movedAnything) {
                        System.exit(0);
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

