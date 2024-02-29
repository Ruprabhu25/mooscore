package com.ecs160;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.swing.border.EmptyBorder;
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
    private int symbolWidth = 0;
    private int symbolHeight = 0;
    private MusicSymbol accidental = null;

    public Symbol(MusicSymbol sym, int x, int y) {
        super();
        this.sym = sym;
        setPreferredSize(new Dimension(sym.width, sym.height)); // Set default size
        setLocation(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        BufferedImage img;
        if (selected) img = sym.highlightImage;
        // if (accidental != null) g2.drawImage(accidental.image, getWidth() + 10 )
        else img = sym.image;
        g2.drawImage(img, 0, 0, symbolWidth, symbolHeight, null);
    }

    public void resize(int size) {
        double largest = (double) Math.max(sym.width, sym.height);
        symbolWidth = (int) (size * sym.width / largest);
        symbolHeight = (int) (size * sym.height / largest);
        setBounds(getX(), getY(), symbolWidth, symbolHeight);
    }

    public void setAccidental(MusicSymbol accidental) {
        // widen symbol to allow for drawing 
    }

    public void select() {
        selected = true;
    }

    public void deselect() {
        selected =false;
    }
}

// can this be a JMenuBar rather than a panel? I think it will be more intuitive. The App example code uses a menu bar
// if you want to see an example 
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

// this class and the menubar class I think shoudl be defined in their own file, this file is getting very large
// i also think these control panels should be decoupled from the TrackGUI panel
class ToolBar extends JPanel {
    public ToolBar(TrackGUI gui) {
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));
        
        // can the width and height of the image icon be set using the w/h of the images themselves? the images should draw
        // without being stretched to a certain shape 
        JButton sixteenthNoteButton = new JButton(new ImageIcon(MusicSymbol.SIXTEENTH.image.getScaledInstance(15,25,Image.SCALE_SMOOTH)));
        JButton eighthNoteButton = new JButton(new ImageIcon(MusicSymbol.EIGHTH.image.getScaledInstance(15,25,Image.SCALE_SMOOTH)));
        JButton quarterNoteButton = new JButton(new ImageIcon(MusicSymbol.QUARTER.image.getScaledInstance(15,25,Image.SCALE_SMOOTH)));
        JButton halfNoteButton = new JButton(new ImageIcon(MusicSymbol.HALF.image.getScaledInstance(15,25,Image.SCALE_SMOOTH)));
        JButton wholeNoteButton = new JButton(new ImageIcon(MusicSymbol.WHOLE.image.getScaledInstance(18,15,Image.SCALE_SMOOTH)));

        // you could create the missing rest enums if you want, just need to define their constructor in MusicSymbol
        // i was thinking of naming them like #NOTE#_REST?
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
                gui.setActiveNote(MusicSymbol.EIGHTH);
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
    private final int gridSize = 10;
    // The number of total grid points top to bottom. 
    // The staff is drawn in the center of this region
    private final int gridHeight = 26;
    // number of blank pixels on left and right of staff lines
    private final int staffBuffer = 100;
    // this is the max dimension of every symbol. for example a quarter note has this height
    private int symbolSize = gridSize * 8;
    // these points define the start and end of the users drag pox
    private Point drag_p1, drag_p2;
    // these are used for measuring movement while dragging components
    private int moust_lastX, mouse_lastY;
    // measure length in quarter notes. this reflects the meter of the track
    private int measureLength = MusicSymbol.RESOLUTION * 4; 
    // the X coordinates of measure bars
    private ArrayList<Point> measureLocations;
    // a list of currently selected symbols
    private ArrayList<Symbol> selected;
    // contains the child components seperated by vertical rows to allow drawing multiple staffs
    private ArrayList<ArrayList<Symbol>> rows; 
    private MusicSymbol activeMusicSymbol;

    private String title = "My Music";
    private String subtitle = "Sub Title";
    private JTextField titleField;
    private JTextField subtitleField;
    private String composer = "Composer";
    private JTextField composerField;

    private JLabel tempoQuarter;
    private int tempo;
    private JTextField tempoField;
    
    public TrackGUI() {
        setLayout(null); // Use absolute positioning
        addTempo();

        measureLocations = new ArrayList<Point>();
        selected = new ArrayList<Symbol>();
        rows = new ArrayList<ArrayList<Symbol>>();
        activeMusicSymbol = null;
        // Add some draggable components
        MusicSymbol imgs[] = {MusicSymbol.QUARTER, MusicSymbol.HALF, MusicSymbol.EIGHTH, MusicSymbol.WHOLE};
        for (int i = 0; i < 1 * imgs.length; i++) {
            createNewSymbol(imgs[i % imgs.length]);
        }
        setFocusable(true); // Enable keyboard focus
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                // Handle key events for panel1
                // Example: move panel1 when arrow keys are pressed
                int keyCode = e.getKeyCode();
                if (e.getID() == KeyEvent.KEY_RELEASED) return false;
                // System.out.println(e.getID());
                int dx = 0; 
                int dy = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        dx = -gridSize;
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        dx = gridSize;
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        dy = -gridSize;
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        dy = gridSize;
                        break;
                }
                for (Symbol s : selected) 
                    moveSymbol(s, s.getX() + dx, s.getY() + dy);
                calculateMeasureLocations();
                return (!selected.isEmpty());
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // check against all components to see if they were clicked on
                System.out.println(e.getX() + ", " + e.getY());
                moust_lastX = e.getX();
                mouse_lastY = e.getY();
                boolean found = false;
                for (Component c :  getComponents()) {
                    if (c instanceof JTextField || c instanceof JLabel)
                        continue;
                    Symbol s = (Symbol) (c);
                    if (s.getBounds().contains(e.getPoint())) {
                        found = true;
                        // if nothing is selected yet, select this
                        if (selected.isEmpty()) select(s);
                        // if something else was selected, clear the selections then select this
                        else if (!selected.contains(s)) {
                            clearSelection();
                            select(s);
                        }
                    }
                }
                // clicked on empty space, deselect everything
                if (!found) clearSelection();
                // set point to allow for drag boxing
                drag_p1 = e.getPoint();
                repaint();
            }
            
            public void mouseReleased(MouseEvent e) {
                // done dragging, reset information
                calculateMeasureLocations(); 
                if (drag_p1 == null || drag_p2 == null) return;
                int minx = Math.min(drag_p1.x, drag_p2.x);
                int maxx = Math.max(drag_p1.x, drag_p2.x);
                int miny = Math.min(drag_p1.y, drag_p2.y);
                int maxy = Math.max(drag_p1.y, drag_p2.y);
                Rectangle rect = new Rectangle(minx, miny, maxx- minx, maxy-miny);
                for (Component c :  getComponents()) {
                    if (c instanceof JTextField || c instanceof JLabel)
                        continue;
                    if (rect.intersects(c.getBounds())) select((Symbol) c);
                }
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
                else drag_p2 = e.getPoint();
                repaint();
            }
        });
    }

    private void addTitlesComposer() {
        titleField = new JTextField(title);
        titleField.setFont(new Font("Arial", Font.BOLD, 24));
        titleField.setBounds(getWidth() / 2, 10, 300, 30);
        titleField.setOpaque(false);
        titleField.setBorder(new EmptyBorder(5, 10, 5, 10));

        subtitleField = new JTextField(subtitle);
        subtitleField.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleField.setBounds(getWidth() / 2, 40, 300, 30);
        subtitleField.setOpaque(false);
        subtitleField.setBorder(new EmptyBorder(5, 10, 5, 10));

        composerField = new JTextField(composer);
        composerField.setFont(new Font("Arial", Font.BOLD, 18));
        composerField.setBounds(getWidth() - 200, 60, 300, 30);
        composerField.setOpaque(false);
        composerField.setBorder(new EmptyBorder(5, 10, 5, 10));

        titleField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                title = titleField.getText();
                repaint();
            }
        });

        subtitleField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                title = titleField.getText();
                repaint();
            }
        });

        composerField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                composer = composerField.getText();
                repaint();
            }
        });

        // Add the text field to the panel
        add(titleField);
        add(subtitleField);
        add(composerField);
    }

    private void addTempo() {
        ImageIcon tempoQuarterIcon = new ImageIcon(MusicSymbol.QUARTER_NOTE_EQUALS.image.getScaledInstance(20,33,Image.SCALE_SMOOTH));
        tempoQuarter = new JLabel(tempoQuarterIcon);
        tempoQuarter.setBounds(10, 60, tempoQuarterIcon.getIconWidth(), tempoQuarterIcon.getIconHeight());
        tempoField = new JTextField("80");
        tempoField.setFont(new Font("Arial", Font.BOLD, 24));
        tempoField.setBounds(30, 70, 50, 30);
        tempoField.setOpaque(false);
        tempoField.setBorder(new EmptyBorder(5, 10, 5, 10));
        tempoField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempo = Integer.valueOf(tempoField.getText());
                repaint();
            }
        });
        add(tempoQuarter);
        add(tempoField);
        
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

    // moves everything in row row between x_start and x_end x_grid_shift spaces
    private void shiftSymbols(int row, int x_start, int x_end, int x_grid_shift) {
        for (Symbol s : rows.get(row)) {
            if (s.getX() < x_start) continue;
            if (s.getX() > x_end) break;
            moveSymbol(s, s.getX() + x_grid_shift * gridSize, s.getY());
        }
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
        calculateMeasureLocations();
    }

    private void createNewSymbol(MusicSymbol newSym) {
        int x = (int) (Math.random() * 400); // Random x position
        int y = (int) (Math.random() * 400); // Random y position
        Point gridPoint = getGridPoint(x, y);
        createNewSymbol(newSym, gridPoint.x, gridPoint.y);
    }

    private void moveSymbol(Symbol s, int x, int y) {
        // clamp x coordinates onto staff
        x = Math.max(staffBuffer, x);
        x = Math.min(x, getWidth() - 100);
        Point gridPoint = getGridPoint(x, y);
        s.setLocation(gridPoint);
    }

    // this should be called after components are done being rearranged
    private void updateRows() {
        rows.clear();
        ArrayList<Component> sorted = getComponentsInXOrder();
        for (Component c : sorted) {
            if (c instanceof JTextField || c instanceof JLabel)
                continue;
            Symbol s = (Symbol) c; // grab first symbol
            int row = s.getY() / (gridHeight * gridSize);
            while (rows.size() < (row + 2)) rows.add(new ArrayList<Symbol>());
            rows.get(row).add(s);
        }
        for (ArrayList<Symbol> row : rows) System.out.println(row.size());
        // System.out.println(rows.size());
        repaint();
    }

    private void calculateMeasureLocations() {
        updateRows();
        measureLocations.clear(); // clear previous
        int ticks = 0;
        for (int r = 0; r < rows.size(); r++) {
            ArrayList<Symbol> row = rows.get(r);
            if (row.isEmpty()) continue;
            Symbol prev = row.get(row.size()-1);
            for (int i = 0; i < row.size(); i++) {
                Symbol s= row.get(i);
                int duration = Math.abs(s.sym.noteDuration);
                // skip non note symbols, overlapping symbols
                if (duration == 0 || s.getX() == prev.getX()) continue;
                prev = s;
                ticks += duration;
                // invalid measure state, break early
                if (ticks > measureLength) return; 
                if (ticks < measureLength) continue;
                // a measure has been completed, add measure location
                ticks = 0;
                int x = s.getX() + gridSize * 7;
                measureLocations.add(new Point(x, r));
                
                if (i == row.size()-1) continue; // this is the last element
                Symbol next = row.get(i+1);
                if (next.getX() < x + gridSize*2) 
                    shiftSymbols(r, next.getX(), getWidth(), ((x - next.getX()) / gridSize) + 2);
                if (next.getX() > (x + gridSize*6)) 
                    shiftSymbols(r, next.getX(), getWidth(), (x - next.getX())/ gridSize + 6);
            }
        }
        System.out.println(measureLocations);
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
        int staffXStart = 100;
        int staffXEnd = getWidth() - 100;
        for (int r = 0; r < rows.size(); r++) {
            int gridCenter = r * (gridHeight) + ((gridHeight / 2) + 1);
            int staffCenter = gridCenter * gridSize;
            for (int line = -2; line <= 2; line++) {
                int y = staffCenter + line * gridSize * 2;
                g2.drawLine(staffXStart, y, staffXEnd, y);
            }
            // draw partial lines below notes that outside the staff
            for (Component c : rows.get(r)) {
                Symbol s = (Symbol) c;
                int gridY = (s.getY() + s.getHeight()) / gridSize;
                // System.out.println(gridC + ", " + gridY);
                if ((gridY > (gridCenter + 4)) || (gridY < (gridCenter - 4))) {
                    for (int i = 0; i < Math.abs(gridY - gridCenter) - 3; i += 2) {
                        int y = (gridCenter + 4 + i) * gridSize; 
                        if ((gridY - gridCenter) < 0) y = -y; 
                        g2.drawLine(s.getX(), y, s.getX() + symbolSize / 5, y);
                    } 
                }
            }
            // draw measure bars
        }
        for (int i = 0; i < measureLocations.size(); i++) {
            Point m = measureLocations.get(i);
            int x = Math.min(m.x, staffXEnd);
            int y = (m.y * (gridHeight) + ((gridHeight / 2) + 1)) * gridSize - 4 * gridSize;
            g2.drawLine(x, y, x, y + gridSize * 8);
            g2.drawString(Integer.toString(i + 1), m.x - gridSize, (y + 5 * gridSize * 2));
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
            frame.setSize(1000, 1000);

            // Create a draggable container
            TrackGUI TrackPanel = new TrackGUI();

            // Create a MenuBar
            MenuBar MenuBar = new MenuBar(TrackPanel);
            ToolBar ToolBar = new ToolBar(TrackPanel);

            // Create a JScrollPane to add the draggable container with scrollbars
            JScrollPane scrollPane = new JScrollPane();
            // scrollPane.setPreferredSize(new Dimension(1000, 1000));
            TrackPanel.setPreferredSize(new Dimension(1000, 1000));
            TrackPanel.setMinimumSize(new Dimension(1000, 1000));
            TrackPanel.setSize(1000, 1000);
            scrollPane.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    // Repaint the content panel when resized
                    scrollPane.repaint();
                    TrackPanel.repaint();
                }
            });
    
            scrollPane.getViewport().add(TrackPanel);
            // Add the MenuBar and scroll pane to the frame
            frame.add(MenuBar, BorderLayout.NORTH);
            frame.add(ToolBar, BorderLayout.WEST);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.setVisible(true);

            TrackPanel.addTitlesComposer();
        });
    }
}
 