package com.ecs160;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.swing.*;
import javax.tools.Tool;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class Symbol extends JComponent {
    public final MusicSymbol sym;
    public boolean selected = false;
    public Symbol(MusicSymbol sym, int x, int y) {
        super();
        this.sym = sym;
        setPreferredSize(new Dimension(sym.width, sym.height)); // Set default size
        setBackground(Color.BLUE); // Set background color
        setOpaque(true); // Make sure the background color is visible
        setLocation(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        // g2.setRenderingHint(RenderingHints.);
        BufferedImage img;
        if (selected) img = sym.highlightImage;
        else img = sym.image;
        g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
    }

    public void resize(int size) {
        double largest = (double) Math.max(sym.width, sym.height);
        int w = (int) (size * sym.width / largest);
        int h = (int) (size * sym.height / largest);
        setBounds(getX(), getY(), w, h);
    }

    public void select() {
        // setBorder(BorderFactory.createLineBorder(Color.blue));
        selected = true;
    }

    public void deselect() {
        // setBorder(BorderFactory.createLineBorder(Color.black));
        selected =false;
    }
}

class MenuBar extends JPanel {
    private String user_guide_message = 
    """
        Hello

        This is the user guide. Info goes here.
    """;
    public MenuBar(TrackGUI gui) {
        // Create a MenuBar
        JMenuBar MenuBar = new JMenuBar();

        // Create buttons for File, Edit, and Help
        JButton fileButton = new JButton("File");
        JButton editButton = new JButton("Edit");
        JButton helpButton = new JButton("Help");

        // Add action listeners to the buttons
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a popup menu for file options
                JPopupMenu popupMenu = new JPopupMenu();
                
                // Create "Save" and "Load" menu items
                JMenuItem saveMenuItem = new JMenuItem("Save");
                JMenuItem loadMenuItem = new JMenuItem("Load");

                saveMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    }
                });
                
                loadMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    }
                });
                
                // Add menu items to the popup menu
                popupMenu.add(saveMenuItem);
                popupMenu.add(loadMenuItem);
                
                // Display the popup menu at the location of the file button
                popupMenu.show(fileButton, 0, fileButton.getHeight());
            }

        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPopupMenu popupMenu = new JPopupMenu();
                
                // Create "Save" and "Load" menu items
                JMenuItem shiftMenuItem = new JMenuItem("shift");
                JMenuItem clearMenuItem = new JMenuItem("clear");

                shiftMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    }
                });
                
                clearMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    }
                });
                
                // Add menu items to the popup menu
                popupMenu.add(shiftMenuItem);
                popupMenu.add(clearMenuItem);
                
                // Display the popup menu at the location of the file button
                popupMenu.show(fileButton, editButton.getWidth(), fileButton.getHeight());
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuBar, user_guide_message, "User Guide", JOptionPane.PLAIN_MESSAGE);
            }
        });

        // Add buttons to the MenuBar
        MenuBar.add(fileButton);
        MenuBar.add(editButton);
        MenuBar.add(helpButton);

        // Add the MenuBar to this panel
        setLayout(new BorderLayout());
        add(MenuBar, BorderLayout.NORTH);
    }
}

class ToolBar extends JPanel {
    public ToolBar(TrackGUI gui) {
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));

        JButton sixteenthNoteButton = new JButton(new ImageIcon(MusicSymbol.SIXTEENTH.image.getScaledInstance(15,25,Image.SCALE_SMOOTH)));
        JButton eighthNoteButton = new JButton(new ImageIcon(MusicSymbol.EIGTH.image.getScaledInstance(15,25,Image.SCALE_SMOOTH)));
        JButton quarterNoteButton = new JButton(new ImageIcon(MusicSymbol.QUARTER.image.getScaledInstance(15,25,Image.SCALE_SMOOTH)));
        JButton halfNoteButton = new JButton(new ImageIcon(MusicSymbol.HALF.image.getScaledInstance(15,25,Image.SCALE_SMOOTH)));
        JButton wholeNoteButton = new JButton(new ImageIcon(MusicSymbol.WHOLE.image.getScaledInstance(18,15,Image.SCALE_SMOOTH)));

        JButton sixteenthRestButton = new JButton();
        JButton eighthRestButton = new JButton();
        JButton quarterRestButton = new JButton();
        JButton halfRestButton = new JButton();
        JButton wholeRestButton = new JButton();

        toolBar.add(sixteenthNoteButton);
        toolBar.add(eighthNoteButton);
        toolBar.add(quarterNoteButton);
        toolBar.add(halfNoteButton);
        toolBar.add(wholeNoteButton);

        //TODO: add rest buttons (need images)

        //add event listeners for current active notes
        sixteenthNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setActiveNote(MusicSymbol.SIXTEENTH);
            }
        });

        eighthNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setActiveNote(MusicSymbol.EIGTH);
            }
        });

        quarterNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setActiveNote(MusicSymbol.QUARTER);
            }
        });

        halfNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setActiveNote(MusicSymbol.HALF);
            }
        });

        wholeNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setActiveNote(MusicSymbol.WHOLE);
            }
        });

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
    }
}

public class TrackGUI extends JPanel {
    // The number of pixels between each grid point
    private int gridSize = 10;
    // The number of total grid points top to bottom. 
    // The staff is drawn in the center of this region
    private int gridHeight = 41;
    private int gridCenter = gridHeight / 2 + 1;
    // this is the max dimension of every symbol. for example a quarter note has this height
    private int symbolSize = gridSize * 8;
    // these points define the start and end of the users drag pox
    private Point drag_p1, drag_p2;
    // these are used for measuring movement while dragging components
    private int moust_lastX, mouse_lastY;
    // measure length in quarter notes. this reflects the meter of the track
    private int measureLength = MusicSymbol.RESOLUTION * 4; 
    // the X coordinates of measure bars
    private ArrayList<Integer> measureLocations;
    // a list of currently selected symbols
    private ArrayList<Symbol> selected;

    private MusicSymbol activeMusicSymbol;
    
    public TrackGUI() {
        setLayout(null); // Use absolute positioning
        measureLocations = new ArrayList<Integer>();
        selected = new ArrayList<Symbol>();
        activeMusicSymbol = MusicSymbol.QUARTER;
        // Add some draggable components
        MusicSymbol imgs[] = {MusicSymbol.QUARTER, MusicSymbol.HALF, MusicSymbol.EIGHTH, MusicSymbol.WHOLE};
        for (int i = 0; i < 5 * imgs.length; i++) {
            createNewSymbol(imgs[i % imgs.length]);
        }

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // check against all components
                moust_lastX = e.getX();
                mouse_lastY = e.getY();
                boolean found = false;
                for (Component c :  getComponents()) {
                    Symbol s = (Symbol) (c);
                    if (s.getBounds().contains(e.getPoint())) {
                        found = true;
                        if (selected.isEmpty()) select(s);
                        else if (!selected.contains(s)) {
                            clearSelection();
                            select(s);
                        }
                    }
                }
                if (!found) {
                    clearSelection();
                }
                drag_p1 = e.getPoint();
                repaint();
            }
            
            public void mouseReleased(MouseEvent e) {
                calculateMeasureLocations();
                if (drag_p1 == null || drag_p2 == null) return;
                int minx = Math.min(drag_p1.x, drag_p2.x);
                int maxx = Math.max(drag_p1.x, drag_p2.x);
                int miny = Math.min(drag_p1.y, drag_p2.y);
                int maxy = Math.max(drag_p1.y, drag_p2.y);
                for (Component c :  getComponents()) {
                    Symbol s = (Symbol) (c); 
                    int x = s.getX();
                    int y = s.getY();
                    if (x >= minx && x <= maxx
                     && y >= miny && y <= maxy) select(s);
                    // else s.deselect();
                }
                // System.out.println(selected);
                drag_p1 = null;
                drag_p2 = null;
        }});
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (!selected.isEmpty()) {
                    int dx = e.getX() - moust_lastX;
                    int dy = e.getY() - mouse_lastY;
                    for (Symbol s : selected) moveSymbol(s, s.getX() + dx, s.getY() + dy);
                    Point lastGridPoint = getGridPoint(moust_lastX + dx, mouse_lastY + dy);
                    moust_lastX = lastGridPoint.x;
                    mouse_lastY = lastGridPoint.y;
                    drag_p2 = null;
                }
                else {
                    drag_p2 = e.getPoint();
                }
                repaint();
            }
        });
    }

    private void select(Symbol s) {
        selected.add(s);
        s.select();
        repaint();
    }

    private void clearSelection() {
        for (Symbol s : selected) 
            s.deselect();
        selected.clear();
        repaint();
    }

    private Point getGridPoint(int x, int y) {
        Point out = new Point(x, y);
        out.x = (out.x / gridSize) * gridSize;
        out.y = (out.y / gridSize) * gridSize;    
        return out;
    }

    private void createNewSymbol(MusicSymbol newSym, int x, int y) {
        Symbol symbol = new Symbol(newSym, x, y);
        symbol.resize((int) (symbolSize * newSym.scale));
        add(symbol);
    }

    private void createNewSymbol(MusicSymbol newSym) {
        int x = (int) (Math.random() * 400); // Random x position
        int y = (int) (Math.random() * 400); // Random y position
        Point gridPoint = getGridPoint(x, y);
        createNewSymbol(newSym, gridPoint.x, gridPoint.y);
    }

    private void moveSymbol(Symbol s, int x, int y) {
        // System.out.println(dx + ", " + dy + ", " + (s.getX() + dx) + ", " + (s.getY() + dy));
        Point gridPoint = getGridPoint(x, y);
        s.setLocation(gridPoint);
    }

    private void calculateMeasureLocations() {
        measureLocations.clear(); // clear previous
        ArrayList<Component> sorted = getComponentsInXOrder();
        int ticks = 0; // counter to track how long the measure is so far
        Symbol prev = (Symbol) sorted.get(sorted.size()-1);
        while (!sorted.isEmpty()) {
            Symbol s = (Symbol) sorted.remove(0); // grab first symbol
            if (s.sym.noteDuration == -1) continue; // skip non note symbols
            if (s.getX() == prev.getX()) continue; // skip stacked notes
            prev = s;
            ticks += s.sym.noteDuration;
            if (ticks > measureLength) break; // early return if a non-valid measure size is found
            if (ticks == measureLength) {
                ticks = 0; 
                int measureLoc = s.getX() + symbolSize; 
                measureLocations.add(measureLoc);
                int i = 0;
                // move symbols that are now within the wrong measure
                while (i < sorted.size() && sorted.get(i).getX() < measureLoc) 
                    moveSymbol((Symbol) sorted.get(i), measureLoc, sorted.get(i++).getY());
            }
        }
        repaint();
    }

    private ArrayList<Component> getComponentsInXOrder() {
        ArrayList<Component> children = new ArrayList<Component>(Arrays.asList(getComponents()));
                children.sort(Comparator.comparing(Component::getX));
        return children;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // try to make it look better ;-; 
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        // draw staff lines
        int staffCenter = (gridCenter) * gridSize;
        // System.out.println(staffCenter);
        for (int line = -2; line <= 2; line++) {
            int y = (staffCenter + line * gridSize * 2);
            g2.drawLine(100, y, getWidth() - 100, y);
        }

        // draw partial lines below notes that outside the staff
        for (Component c : getComponents()) {
            Symbol s = (Symbol) c;
            int gridY = (s.getY() + s.getHeight()) / gridSize;
            // System.out.println(gridC + ", " + gridY);
            if ((gridY > (gridCenter + 4)) || (gridY < (gridCenter - 4))) {
                for (int i = 0; i < Math.abs(gridY - gridCenter) - 2; i += 2) {
                    int y = (gridCenter + 4 + i) * gridSize; 
                    if ((gridY - gridCenter) < 0) y = -y; 
                    g2.drawLine(s.getX(), y, s.getX() + symbolSize / 5, y);
                } 
            }
        }
        // draw measure bars
        for (int i = 0; i < measureLocations.size(); i++) {
            int m = measureLocations.get(i);
            g2.drawLine(m, staffCenter - 2 * gridSize * 2, m, staffCenter + 2 * gridSize * 2);
            g2.drawString(Integer.toString(i), m, staffCenter + 4 * gridSize * 2);
        }
        // draw mouse drag box
        if (drag_p1 == null || drag_p2 == null) return;
        int minx = Math.min(drag_p1.x, drag_p2.x);
        int maxx = Math.max(drag_p1.x, drag_p2.x);
        int miny = Math.min(drag_p1.y, drag_p2.y);
        int maxy = Math.max(drag_p1.y, drag_p2.y);
        g2.setColor(Color.BLUE);
        g2.drawRect(minx, miny, maxx - minx, maxy - miny);
    }

    public Track getMidiTrack(Sequence sequence) {
        Track track = sequence.createTrack();
        ArrayList<Component> sorted = getComponentsInXOrder();
                return track;
    } 

    public void setActiveNote(MusicSymbol note) {
        this.activeMusicSymbol = note;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a JFrame
            JFrame frame = new JFrame("Draggable Container");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);

            // Create a draggable container
            TrackGUI draggableContainer = new TrackGUI();

            // Create a MenuBar
            MenuBar MenuBar = new MenuBar(draggableContainer);
            ToolBar ToolBar = new ToolBar(draggableContainer);

            // Create a JScrollPane to add the draggable container with scrollbars
            JScrollPane scrollPane = new JScrollPane(draggableContainer);

            // Set the preferred size of the scroll pane (optional)
            scrollPane.setPreferredSize(new Dimension(500, 400));

            // Add the MenuBar and scroll pane to the frame
            frame.add(MenuBar, BorderLayout.NORTH);
            frame.add(ToolBar, BorderLayout.WEST);
            frame.add(scrollPane, BorderLayout.CENTER);

            // Make the frame visible
            frame.setVisible(true);
        });
    }
}
