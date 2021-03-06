package pingus;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

public class Main {

    private static final LinkedList<Pair> serverList = new LinkedList<>();

    private static void createWindow(String serversfile) {
        final JFrame frame = new JFrame("Pingus");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(626, 283));

        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEADING));

        List<Pair> servers = Ping.getPairs(serversfile);

        for (final Pair p : servers) {
            final JTextPane keyLabel = new JTextPane();
            final JTextPane pingLabel = new JTextPane();
            JPanel group = new JPanel(new FlowLayout(FlowLayout.LEADING));
            group.add(keyLabel);
            group.add(pingLabel);
            frame.add(group);

            keyLabel.setContentType("text/html");
            pingLabel.setContentType("text/html");

            keyLabel.setText("<html>" + p.getKey() + "/" + "<a href=\"http://" + p.getValue() + "\">" + p.getValue() + "</a>" + "</html>");

            keyLabel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    StringSelection stringSelection = new StringSelection(p.getValue());
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clpbrd.setContents(stringSelection, null);
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        int ping = Ping.pingOnce(p.getValue());
                        String pingString = "<strong>" + Integer.toString(ping) + "</strong>";

                        if (ping <= -1) {
                            pingString = "<font color=\"red\">" + "<strong>timeout</strong>" + "</font>";
                        } else if (ping > -1 && ping <= 50) {
                            pingString = "<font color=\"green\">" + pingString + "</font>";
                        } else if (ping > 50) {
                            pingString = "<font color=\"maroon\">" + pingString + "</font>";
                        }

                        pingLabel.setText("<html>" + pingString + "</html>");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }.start();
        }

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Pingus. Pings a list of servers once a second.");
            System.out.println("    Usage: java -jar Pingus.jar path_to_serversfile");
            System.out.println("    the serversfile is a file of the form: ");
            System.out.println("        name1 address1");
            System.out.println("        name2 address2");
            System.out.println("        ...");
            System.exit(1);
        }
        createWindow(args[0]);
    }
}
