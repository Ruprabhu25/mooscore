package com.ecs160;

import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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
                        saveToFile(gui);
                    }
                });
                
                loadMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loadFromFile(gui);
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
                JMenuItem selectAllMenuItem = new JMenuItem("select all");
                JMenuItem clearMenuItem = new JMenuItem("clear");

                selectAllMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (Component c : gui.getComponents()) {
                            if (c instanceof Symbol) {
                                gui.select((Symbol) c);
                            }
                        }
                    }
                });
                
                clearMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (Component c : gui.getComponents()) {
                            if (c instanceof Symbol) {
                                gui.remove(c);
                            }
                        }
                        repaint();
                    }
                });
                
                // Add menu items to the popup menu
                popupMenu.add(selectAllMenuItem);
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

    private void saveToFile(TrackGUI gui) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            String selectedDirectory = fileChooser.getSelectedFile().getAbsolutePath();
            System.out.println("Selected Directory: " + selectedDirectory);
            File file = new File(selectedDirectory + File.separator + gui.getTitle() + ".dat");
            Component[] components = gui.getComponents();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                for (Component component : components) {
                    oos.writeObject(component);
                }
                System.out.println("Components saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadFromFile(TrackGUI gui) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            gui.removeAll();
            gui.revalidate();
            gui.repaint();
            String selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile))) {
            while (true) {
                try {
                    Component component = (Component) ois.readObject();
                    if (component instanceof Symbol) {
                        Symbol s = (Symbol) component;
                        gui.createNewSymbol(s.sym, s.getX(), s.getY());
                    }
                    else {
                        gui.add(component);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
        }
        } catch (IOException e) {
            // End of file reached or file does not exist
            System.out.println("Components loaded successfully.");
        }
    }
}
}

class ToolBar extends JPanel {
    // private void addAccidentalButton(MusicSymbol sym, JToolBar toolBar)
    
    private void addSymbolButton(MusicSymbol sym, JToolBar toolBar, TrackGUI gui) {
        int image_size = 50;
        BufferedImage icon = new BufferedImage(image_size, image_size, BufferedImage.TYPE_INT_ARGB);
        Image sym_image = MusicSymbol.getScaledImage(sym, (int) (image_size / sym.scale));
        icon.getGraphics().drawImage(sym_image, 
            (image_size - sym_image.getWidth(null)) / 2, 
            (image_size - sym_image.getHeight(null)) / 2, null);
        JButton button = new JButton(new ImageIcon(icon));
        toolBar.add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setActiveNote(sym);
            }
        });
    }

    private void addAccidentalButton(MusicSymbol accidental, JToolBar toolBar, TrackGUI gui) {
        int image_size = 50;
        BufferedImage icon = new BufferedImage(image_size, image_size, BufferedImage.TYPE_INT_ARGB);
        Image sym_image = MusicSymbol.getScaledImage(accidental, (int) (image_size / accidental.scale));
        icon.getGraphics().drawImage(sym_image, 
            (image_size - sym_image.getWidth(null)) / 2, 
            (image_size - sym_image.getHeight(null)) / 2, null);
        JButton button = new JButton(new ImageIcon(icon));

        toolBar.add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Symbol s : gui.selected) {
                    if (s.getAccidental() == accidental) s.setAccidental(null);
                    else s.setAccidental(accidental);
                }
            }
        });
    }

    public ToolBar(TrackGUI gui) {
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));
        
        // add buttons
        MusicSymbol notes[] = {MusicSymbol.SIXTEENTH, MusicSymbol.EIGHTH, 
                    MusicSymbol.QUARTER, MusicSymbol.HALF, MusicSymbol.WHOLE};
        for (MusicSymbol n : notes) addSymbolButton(n, toolBar, gui);
        
        MusicSymbol rests[] = {MusicSymbol.SIXTEENTH_REST, MusicSymbol.EIGTH_REST,
            MusicSymbol.QUARTER_REST, MusicSymbol.HALF_REST};
        for (MusicSymbol r : rests) addSymbolButton(r, toolBar, gui);

        MusicSymbol accidentals[] = {MusicSymbol.FLAT, MusicSymbol.SHARP, MusicSymbol.NATURAL};
        for (MusicSymbol accidental : accidentals) addAccidentalButton(accidental, toolBar, gui);
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
    private int x_staffBuffer = 100;
    // this is the max dimension of every symbol. for example a quarter note has this height
    private int symbolSize = gridSize * 8;
    // these points define the start and end of the users drag pox
    private Point drag_p1, drag_p2;
    // these are used for measuring movement while dragging components
    private int last_clickX, last_clickY, mouseX, mouseY;
    // measure length in quarter notes. this reflects the meter of the track
    private int measureLength = MusicSymbol.RESOLUTION * 4; 
    // the X coordinates of measure bars
    private ArrayList<Point> measureLocations;
    // a list of currently selected symbols
    public ArrayList<Symbol> selected;
    // contains the child components seperated by vertical rows to allow drawing multiple staffs
    private ArrayList<ArrayList<Symbol>> rows; 
    private MusicSymbol activeMusicSymbol;

    // this keeps track of if the track has been changed in between playback

    private JTextField titleField;
    private JTextField subtitleField;
    private JTextField composerField;

    private JLabel tempoQuarter;
    private int tempo = 80;
    private JTextField tempoField;
    
    public TrackGUI() {
        setLayout(null); // Use absolute positioning
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        int maxWidth = Math.min(1500, (int) (gd.getDisplayMode().getWidth() * 0.4));
        // set max size, 1.29 times taller than wider for 8.5x11 feel
        Dimension trackDimension = new Dimension((int) maxWidth, (int) (maxWidth * 1.29));
        // System.out.println("dim: " + trackDimension);
        setMaximumSize(trackDimension);
        setMinimumSize(trackDimension);
        setPreferredSize(trackDimension);

        // add text inputs
        addTempo();
        addTitlesComposer();

        // initialize global arrays
        measureLocations = new ArrayList<Point>();
        selected = new ArrayList<Symbol>();
        rows = new ArrayList<ArrayList<Symbol>>();
        updateRows();
        activeMusicSymbol = null;


        setFocusable(true); // Enable keyboard focus
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                // only handle key presses and holds, ignore when the key is released
                if (e.getID() != KeyEvent.KEY_PRESSED) return false;
                
                // these will potentially be set below
                int dx = 0; 
                int dy = 0;
                MusicSymbol newAccidental = MusicSymbol.BREATH_MARK; // placeholder flag
                switch (e.getKeyCode()) {

                    // moving selected items
                    
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
                    
                    // add accidentals
                    case KeyEvent.VK_F:
                        newAccidental = MusicSymbol.FLAT;
                        break;
                    case KeyEvent.VK_N:
                        newAccidental = MusicSymbol.NATURAL;
                        break;
                    case KeyEvent.VK_H:
                        newAccidental = MusicSymbol.SHARP;
                        break;
                    case KeyEvent.VK_G:
                        newAccidental = null;
                        break;

                    // select notes to place
                    case KeyEvent.VK_1:
                        if (e.isShiftDown()) setActiveNote(MusicSymbol.WHOLE_REST);
                        else setActiveNote(MusicSymbol.WHOLE);
                        break;
                    case KeyEvent.VK_2:
                        if (e.isShiftDown()) setActiveNote(MusicSymbol.HALF_REST);
                        else setActiveNote(MusicSymbol.HALF);
                        break;
                    case KeyEvent.VK_3:
                        if (e.isShiftDown()) setActiveNote(MusicSymbol.QUARTER_REST);
                        else setActiveNote(MusicSymbol.QUARTER);
                        break;
                    case KeyEvent.VK_4:
                        if (e.isShiftDown()) setActiveNote(MusicSymbol.EIGTH_REST);
                        else setActiveNote(MusicSymbol.EIGHTH);
                        break;
                    case KeyEvent.VK_5:
                        if (e.isShiftDown()) setActiveNote(MusicSymbol.SIXTEENTH_REST);
                        else setActiveNote(MusicSymbol.SIXTEENTH);
                        break;

                    // delete selected items
                    case KeyEvent.VK_BACK_SPACE:
                    case KeyEvent.VK_DELETE:
                        activeMusicSymbol = null; // clear active note
                        for (Symbol s: selected) remove(s);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        activeMusicSymbol = null; // clear active note
                        break;
                    default: 
                        clearSelection();
                }
                for (Symbol s : selected) {
                    moveSymbol(s, s.getX() + dx, s.getY() + dy);
                    if (newAccidental != MusicSymbol.BREATH_MARK) {
                        if (s.getAccidental() == newAccidental) s.setAccidental(null);
                        else s.setAccidental(newAccidental);
                    }
                }
                calculateMeasureLocations();
                return (!selected.isEmpty());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drag_p1 = e.getPoint();
                
                last_clickX = e.getX();
                last_clickY = e.getY();
                // check if shift is held
                boolean shift_down = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
                boolean found = false;
                
                // check against all components to see if they were clicked on
                for (Component c :  getComponents()) {
                    if (!(c instanceof Symbol)) // only care about symbols
                        continue;
                    Symbol s = (Symbol) (c);
                    // clicked on a symbol
                    if (s.getBounds().contains(e.getPoint())) {
                        found = true;
                        // already selected, no need to do anything
                        if (selected.contains(s)) continue;
                        // if shift is not held, clear previous selection
                        if (!shift_down) clearSelection();
                        // if nothing is selected yet, select this
                        select(s);
                    }
                }
                requestFocusInWindow();
                // clicked on a component
                if (found) {
                    activeMusicSymbol = null;
                    repaint();
                    return;
                }

                // clicked on empty space, deselect everything
                clearSelection();
                
                // check if drawing new symbol
                if (activeMusicSymbol != null) {
                    createNewSymbol(activeMusicSymbol, e.getX(), 
                        e.getY() - (int) (symbolSize * activeMusicSymbol.scale));
                    if (!shift_down)
                        activeMusicSymbol = null;
                }

                // set point to allow for drag boxing
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // done dragging, reset information
                calculateMeasureLocations(); 
                if (drag_p1 == null || drag_p2 == null) return;
                int minx = Math.min(drag_p1.x, drag_p2.x);
                int maxx = Math.max(drag_p1.x, drag_p2.x);
                int miny = Math.min(drag_p1.y, drag_p2.y);
                int maxy = Math.max(drag_p1.y, drag_p2.y);
                Rectangle rect = new Rectangle(minx, miny, maxx- minx, maxy-miny);
                for (Symbol c :  getSymbolsInXOrder()) {
                    if (rect.intersects(c.getBounds())) select((Symbol) c);
                }
                drag_p1 = null;
                drag_p2 = null;
        }});
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!selected.isEmpty()) {
                    int dx = e.getX() - last_clickX;
                    int dy = e.getY() - last_clickY;
                    for (Symbol s : selected) moveSymbol(s, s.getX() + dx, s.getY() + dy);
                    Point lastGridPoint = getGridPoint(last_clickX + dx, last_clickY + dy);
                    last_clickX = lastGridPoint.x;
                    last_clickY = lastGridPoint.y;
                    drag_p2 = null;
                }
                else drag_p2 = e.getPoint();
                repaint();
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });
        
        addComponentListener( new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // reset the staff buffer if necessary
                if (getWidth() > getMaximumSize().width) {
                    x_staffBuffer = (int) ((getWidth() - getMaximumSize().width) / 2);
                } 
            };
        });
    }

    public String getTitle() {
        return titleField.getText();
    }

    private void addTitlesComposer() {
        titleField = new JTextField("My Music");
        titleField.setFont(new Font("Arial", Font.BOLD, 24));
        titleField.setBounds(getPreferredSize().width / 2, 10, 300, 30);
        titleField.setOpaque(false);
        titleField.setBorder(new EmptyBorder(5, 10, 5, 10));

        subtitleField = new JTextField("Sub Title");
        subtitleField.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleField.setBounds(getPreferredSize().width / 2, 40, 300, 30);
        subtitleField.setOpaque(false);
        subtitleField.setBorder(new EmptyBorder(5, 10, 5, 10));

        composerField = new JTextField("Composer");
        composerField.setFont(new Font("Arial", Font.BOLD, 18));
        composerField.setBounds(getPreferredSize().width - 200, 60, 300, 30);
        composerField.setOpaque(false);
        composerField.setBorder(new EmptyBorder(5, 10, 5, 10));

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
                System.out.println("Tempo changed to " + tempo);
                repaint();
            }
        });
        add(tempoQuarter);
        add(tempoField);
        
    }

    protected void select(Symbol s) {
        // make sure the same symbol is not selected twice
        if (selected.contains(s)) return;
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
            if (s.getSymbolX() < x_start) continue;
            if (s.getSymbolX() > x_end) break;
            moveSymbol(s, s.getX() + x_grid_shift * gridSize, s.getY());
        }
    }

    private Point getGridPoint(int x, int y) {
        Point out = new Point(x, y);
        out.x = (out.x / gridSize) * gridSize;
        out.y = (out.y / gridSize) * gridSize;    
        return out;
    }

    protected void createNewSymbol(MusicSymbol newSym, int x, int y) {
        // align symbol to grid
        Point grid_point = getGridPoint(x, y);
        Symbol symbol = new Symbol(newSym, grid_point.x, grid_point.y);
        symbol.resize((int) (symbolSize * newSym.scale));
        add(symbol);
        moveSymbol(symbol, grid_point.x, grid_point.y);
        calculateMeasureLocations();
    }

    private void moveSymbol(Symbol s, int x, int y) {
        // clamp x coordinates onto staff
        x = Math.max(x_staffBuffer, x);
        x = Math.min(x, getWidth() - x_staffBuffer);
        
        // clamp y coordinates onto screen
        y = Math.max(0, y);
        y = Math.min(getHeight(), y);
        Point gridPoint = getGridPoint(x, y);
        s.setLocation(gridPoint);
    }

    // this should be called after components are done being rearranged
    private void updateRows() {
        rows.clear();
        ArrayList<Symbol> sorted = getSymbolsInXOrder();
        rows.add(new ArrayList<Symbol>()); // add first row
        for (Symbol s : sorted) {
            int row = s.getBottomY() / (gridHeight * gridSize);
            while (rows.size() < (row + 2)) rows.add(new ArrayList<Symbol>());
            rows.get(row).add(s);
        }
        // for (ArrayList<Symbol> row : rows) System.out.println(row.size());
        repaint();
    }

    private void calculateMeasureLocations() {
        updateRows();
        measureLocations.clear(); // clear previous
        int ticks = 0;
        for (int r = 0; r < rows.size(); r++) {
            // get all symbols in a row
            ArrayList<Symbol> row = rows.get(r);
            if (row.isEmpty()) continue;
            // initialize the prev symbol as none
            Symbol prev = null;
            for (int i = 0; i < row.size(); i++) {
                Symbol s= row.get(i);
                int duration = Math.abs(s.sym.noteDuration);
                // symbols with duration 0 are not notes
                if (duration == 0) continue;
                // skip overlapping
                if (prev != null && prev.getSymbolX() == s.getSymbolX()) continue;
                
                // unique new symbol was found
                prev = s;
                ticks += duration;
                // measure overflowed, return early
                if (ticks > measureLength) return; 
                if (ticks < measureLength) continue;
                
                // a measure has been completed, add measure location
                ticks = 0;
                int x = s.getSymbolX() + gridSize * 7;
                measureLocations.add(new Point(x, r));
                
                // shift following notes to fit within measures 
                if (i == row.size()-1) continue; // this is the last element
                Symbol next = row.get(i+1);

                // if it is too close, move it away 
                if (next.getSymbolX() < (x + gridSize*2)) 
                    shiftSymbols(r, next.getSymbolX(), getWidth(), ((x - next.getSymbolX()) / gridSize) + 2);
                
                    // if it is too far, move it closer
                if (next.getSymbolX() > (x + gridSize*6)) 
                    shiftSymbols(r, next.getSymbolX(), getWidth(), (x - next.getSymbolX())/ gridSize + 6);
            }
        }
    }

    private ArrayList<Symbol> getSymbolsInXOrder() {
        ArrayList<Component> children = new ArrayList<Component>(Arrays.asList(getComponents()));
        
        // grab only children that are symbols 
        ArrayList<Symbol> symbols = new ArrayList<Symbol>();
        for (Component c : children) 
            if (c instanceof Symbol) symbols.add((Symbol) c);
            
        // sort by their x location on the screen 
        symbols.sort(Comparator.comparing(Symbol::getSymbolX));
        return symbols;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // try to make it look better ;-; 
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        // Symbol treble_clef = 

        // draw staff lines
        int staffXStart = x_staffBuffer - symbolSize / 2;
        int staffXEnd = getWidth() - x_staffBuffer;
        for (int r = 0; r < rows.size(); r++) {
            int gridCenter = r * (gridHeight) + ((gridHeight / 2) + 1);
            int staffCenter = gridCenter * gridSize;
            for (int line = -2; line <= 2; line++) {
                int y = staffCenter + line * gridSize * 2;
                g2.drawLine(staffXStart, y, staffXEnd, y);
            }

            // draw partial lines below notes that are outside the staff
            for (Component c : rows.get(r)) {
                Symbol s = (Symbol) c;
                int gridY = s.getBottomY() / gridSize;
                int lineStart, lineEnd;
                if (gridY > (gridCenter + 4)) {
                    lineStart = gridCenter + 4;
                    lineEnd = ((gridY + 1) / 2) * 2;
                }
                else if (gridY < (gridCenter - 4)) {
                    lineStart = (gridY / 2) * 2;
                    lineEnd = gridCenter - 4;
                }
                else continue;
                for (int i = 0; (lineStart + i) < lineEnd; i += 2) {
                    int y = (lineStart + i) * gridSize; 
                    g2.drawLine(s.getSymbolX() + 3, y, s.getSymbolX() + symbolSize / 5, y);
                }
            }

            // draw clef
            g2.drawImage(MusicSymbol.getScaledImage(MusicSymbol.TREBLE, symbolSize), staffXStart, 
                    staffCenter - 4 * gridSize, null);
        }

        // draw measure bars
        for (int i = 0; i < measureLocations.size(); i++) {
            Point m = measureLocations.get(i);
            int x = Math.min(m.x, staffXEnd);
            int y = (m.y * (gridHeight) + ((gridHeight / 2) + 1)) * gridSize - 4 * gridSize;
            g2.drawLine(x, y, x, y + gridSize * 8);
            g2.drawString(Integer.toString(i + 1), m.x - gridSize, (y + 5 * gridSize * 2));
        }

        // draw mouse drag box
        Rectangle mouseBox = getMouseDragBox();
        if (mouseBox != null) {
            g2.setColor(new Color(93, 164, 227, 128));
            g2.fill(mouseBox);
        }

        // draw active note if applicable
        if (activeMusicSymbol != null) 
            g2.drawImage(MusicSymbol.getScaledImage(activeMusicSymbol, symbolSize,true), 
            mouseX, mouseY - (int) (symbolSize * activeMusicSymbol.scale), null);
    }

    private Rectangle getMouseDragBox() {
        if (drag_p1 == null || drag_p2 == null) return null;
        int minx = Math.min(drag_p1.x, drag_p2.x);
        int maxx = Math.max(drag_p1.x, drag_p2.x);
        int miny = Math.min(drag_p1.y, drag_p2.y);
        int maxy = Math.max(drag_p1.y, drag_p2.y);
        return new Rectangle(minx, miny, maxx - minx, maxy - miny);
    }

    public void setActiveNote(MusicSymbol note) {
        this.activeMusicSymbol = note;
    }

    public ArrayList<ArrayList<Symbol>> getRows() {
        return this.rows;
    }
}
