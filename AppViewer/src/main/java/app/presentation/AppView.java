package app.presentation;

import app.domain.EBike;
import app.domain.RentalService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;


public class AppView extends JFrame {

    private static final long POLLING_TIME = 100;
    private VisualiserPanel centralPanel;
    private RentalService service;
    private JPanel topPanel = new JPanel();

    public AppView(RentalService service){
        this.service = service;
        setupView();
    }

    protected void setupView() {
        setTitle("Rental Service Viewer");
        setSize(800,600);
        setResizable(false);

        setLayout(new BorderLayout());

        centralPanel = new VisualiserPanel(800,500,service);
        add(centralPanel,BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });

        startPolling();
    }

    /**
     * Instead of being notified by the domain, it uses polling to update itself
     */
    private void startPolling() {
        new Thread(() -> {
            while (true) {
                try {
                    log("Poll service...");
                    refreshView();
                    log("Done");
                    Thread.sleep(POLLING_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void display() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }

    public void refreshView() {
        centralPanel.refresh();
    }

    private void log(String msg) {
        System.out.println("[EBikeApp] " + msg);
    }

    public static class VisualiserPanel extends JPanel {
        private long dx;
        private long dy;
        private RentalService service;

        public VisualiserPanel(int w, int h, RentalService app){
            setSize(w,h);
            dx = w/2 - 20;
            dy = h/2 - 20;
            this.service = app;
        }

        public void paint(Graphics g){
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0,0,this.getWidth(),this.getHeight());

            for (EBike b : service.getBikes()) {
                int x0 = (int) (dx + b.xPos());
                int y0 = (int) (dy - b.yPos());
                g2.drawOval(x0, y0, 20, 20);
                g2.drawString(b.id(), x0, y0 + 35);
                g2.drawString("(" + (int) b.xPos() + "," + (int) b.yPos() + ")", x0, y0 + 50);
            }

            var it2 = service.getUsers().iterator();
            var y = 20;
            while (it2.hasNext()) {
                var u = it2.next();
                g2.drawRect(10,y,20,20);
                g2.drawString(u.username() + " - credit: " + u.credits(), 35, y+15);
                y += 25;
            };

        }

        public void refresh(){
            repaint();
        }
    }



//	public static void main(String[] args) {
//		var w = new EBikeApp();
//		w.display();
//	}

}
