import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BouncingMeteorite extends JPanel implements Runnable {
    private int WIDTH = 800;
    private int HEIGHT = 600;
    private int RENDER_WIDTH = 80;
    private int RENDER_HEIGHT = 80;

    // Array holding all loaded textures
    private BufferedImage[] IMAGES = loadImages();

    private List<Meteorite> meteorites = new ArrayList<>();
    private Thread gameThread;
    private boolean running = false;

    // Helper class for individual meteorite state
    private class Meteorite {
        double x, y;
        double xSpeed, ySpeed;
        BufferedImage image;

        public Meteorite(double x, double y, double xSpeed, double ySpeed, BufferedImage image) {
            this.x = x;
            this.y = y;
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
            this.image = image;
        }

        public void update(int panelWidth, int panelHeight) {
            x += xSpeed;
            y += ySpeed;

            if (x <= 0 || x + RENDER_WIDTH >= panelWidth) {
                xSpeed = -xSpeed;
            }
            if (y <= 0 || y + RENDER_HEIGHT >= panelHeight) {
                ySpeed = -ySpeed;
            }
        }

        public void draw(Graphics g) {
            g.drawImage(image, (int) x, (int) y, RENDER_WIDTH, RENDER_HEIGHT, null);
        }
    }

    public BouncingMeteorite(int numMeteorites) {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);

        Random rand = new Random();

        // Spawn the specified number of meteorites with random positions, speeds, and textures
        for (int i = 0; i < numMeteorites; i++) {
            double x = rand.nextInt(WIDTH - RENDER_WIDTH);
            double y = rand.nextInt(HEIGHT - RENDER_HEIGHT);

            double speedX = 2.0 + rand.nextDouble() * 3.0;
            double speedY = 2.0 + rand.nextDouble() * 3.0;

            double xSpeed = rand.nextBoolean() ? speedX : -speedX;
            double ySpeed = rand.nextBoolean() ? speedY : -speedY;

            // Pick a random texture out of the 5 loaded images
            BufferedImage randomImage = IMAGES[rand.nextInt(IMAGES.length)];

            meteorites.add(new Meteorite(x, y, xSpeed, ySpeed, randomImage));
        }
    }

    private static BufferedImage[] loadImages() {
        BufferedImage[] images = new BufferedImage[5];
        for (int i = 0; i < 5; i++) {
            String path = "texture/meteorite_" + (i + 1) + ".png";
            try {
                images[i] = ImageIO.read(new File(path));
            } catch (IOException e) {
                throw new RuntimeException("Fatal error: Could not load " + path, e);
            }
        }
        return images;
    }

    public void startThread() {
        if (gameThread == null) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public void run() {
        final double FPS = 60.0;
        final double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (running) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        // 1. Move all meteorites and bounce off walls
        for (Meteorite m : meteorites) {
            m.update(getWidth(), getHeight());
        }

        // 2. Check for collisions between meteorites
        for (int i = 0; i < meteorites.size(); i++) {
            for (int j = i + 1; j < meteorites.size(); j++) {
                Meteorite m1 = meteorites.get(i);
                Meteorite m2 = meteorites.get(j);

                if (checkCollision(m1, m2)) {
                    resolveCollision(m1, m2);
                }
            }
        }
    }

    // Axis-Aligned Bounding Box (AABB) intersection check
    private boolean checkCollision(Meteorite m1, Meteorite m2) {
        return m1.x < m2.x + RENDER_WIDTH &&
            m1.x + RENDER_WIDTH > m2.x &&
            m1.y < m2.y + RENDER_HEIGHT &&
            m1.y + RENDER_HEIGHT > m2.y;
    }

    // Elastic bounce resolution
    private void resolveCollision(Meteorite m1, Meteorite m2) {
        // Determine horizontal vs vertical impact alignment
        double dx = (m1.x + RENDER_WIDTH / 2.0) - (m2.x + RENDER_WIDTH / 2.0);
        double dy = (m1.y + RENDER_HEIGHT / 2.0) - (m2.y + RENDER_HEIGHT / 2.0);

        if (Math.abs(dx) > Math.abs(dy)) {
            // Swap horizontal velocities
            double tempX = m1.xSpeed;
            m1.xSpeed = m2.xSpeed;
            m2.xSpeed = tempX;
        } else {
            // Swap vertical velocities
            double tempY = m1.ySpeed;
            m1.ySpeed = m2.ySpeed;
            m2.ySpeed = tempY;
        }

        // Separate overlapping sprites slightly to prevent sticking
        if (dx > 0) {
            m1.x += 1;
            m2.x -= 1;
        } else {
            m1.x -= 1;
            m2.x += 1;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Meteorite m : meteorites) {
            m.draw(g);
        }
    }


}