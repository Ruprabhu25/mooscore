package com.ecs160;

import javax.sound.midi.*;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


// TODO:  can this be a JMenuBar rather than a panel? I think it will be more intuitive. The App example code uses a menu bar
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

class ToolBar extends JPanel {
    // private void addAccidentalButton(MusicSymbol sym, JToolBar toolBar)
    
    private void addSymbolButton(MusicSymbol sym, JToolBar toolBar, TrackGUI gui) {
        int image_size = 50;
        JButton button = new JButton(new ImageIcon(MusicSymbol.getScaledImage(sym, image_size)));
        toolBar.add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setActiveNote(sym);
            }
        });
    }

    public ToolBar(TrackGUI gui) {
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));
        
        // TODO: can the width and height of the image icon be set using the w/h of the images themselves? the images should draw
        // without being stretched to a certain shape
        // add buttons
        addSymbolButton(MusicSymbol.SIXTEENTH, toolBar, gui);
        addSymbolButton(MusicSymbol.EIGHTH, toolBar, gui);
        addSymbolButton(MusicSymbol.QUARTER, toolBar, gui);
        addSymbolButton(MusicSymbol.HALF, toolBar, gui);
        addSymbolButton(MusicSymbol.WHOLE, toolBar, gui);

        addSymbolButton(MusicSymbol.SIXTEENTH_REST, toolBar, gui);
        addSymbolButton(MusicSymbol.EIGTH_REST, toolBar, gui);
        addSymbolButton(MusicSymbol.QUARTER_REST, toolBar, gui);
        addSymbolButton(MusicSymbol.HALF_REST, toolBar, gui);
        addSymbolButton(MusicSymbol.WHOLE_REST, toolBar, gui);

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
        // set limits on the size of the component
        setMaximumSize(new Dimension(2000, 1800)); 
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
                // only handle key presses and holds, ignore when the key is released
                if (e.getID() != KeyEvent.KEY_PRESSED) return false;
                int dx = 0; 
                int dy = 0;
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
                        for (Symbol s : selected) s.setAccidental(MusicSymbol.FLAT);
                        break;
                    case KeyEvent.VK_N:
                        for (Symbol s : selected) s.setAccidental(MusicSymbol.NATURAL);
                        break;
                    case KeyEvent.VK_H:
                        for (Symbol s : selected) s.setAccidental(MusicSymbol.SHARP);
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
                for (Symbol s : selected) 
                    moveSymbol(s, s.getSymbolX() + dx, s.getY() + dy);
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
                    createNewSymbol(activeMusicSymbol, e.getX(), e.getY() - symbolSize);
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
                for (Component c :  getComponentsInXOrder()) {
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
                    for (Symbol s : selected) moveSymbol(s, s.getSymbolX() + dx, s.getSymbolY() + dy);
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

        // titleField.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         title = titleField.getText();
        //         repaint();
        //     }
        // });

        // subtitleField.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         title = titleField.getText();
        //         repaint();
        //     }
        // });

        // composerField.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         composer = composerField.getText();
        //         repaint();
        //     }
        // });

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
            moveSymbol(s, s.getSymbolX() + x_grid_shift * gridSize, s.getY());
        }
    }

    private Point getGridPoint(int x, int y) {
        Point out = new Point(x, y);
        out.x = (out.x / gridSize) * gridSize;
        out.y = (out.y / gridSize) * gridSize;    
        return out;
    }

    private void createNewSymbol(MusicSymbol newSym, int x, int y) {
        // align symbol to grid
        Point grid_point = getGridPoint(x, y);
        Symbol symbol = new Symbol(newSym, grid_point.x, grid_point.y);
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
        x = Math.max(x_staffBuffer, x);
        x = Math.min(x, getWidth() - x_staffBuffer);
        Point gridPoint = getGridPoint(x, y);
        s.setLocation(gridPoint);
    }

    // this should be called after components are done being rearranged
    private void updateRows() {
        rows.clear();
        ArrayList<Component> sorted = getComponentsInXOrder();
        for (Component c : sorted) {
            Symbol s = (Symbol) c; // grab first symbol
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
            ArrayList<Symbol> row = rows.get(r);
            if (row.isEmpty()) continue;
            Symbol prev = row.get(row.size()-1);
            for (int i = 0; i < row.size(); i++) {
                Symbol s= row.get(i);
                int duration = Math.abs(s.sym.noteDuration);
                // skip non note symbols, overlapping symbols
                if (duration == 0 || s.getSymbolX() == prev.getSymbolX()) continue;
                prev = s;
                ticks += duration;
                // invalid measure state, break early
                if (ticks > measureLength) return; 
                if (ticks < measureLength) continue;
                // a measure has been completed, add measure location
                ticks = 0;
                int x = s.getSymbolX() + gridSize * 7;
                measureLocations.add(new Point(x, r));
                
                if (i == row.size()-1) continue; // this is the last element
                Symbol next = row.get(i+1);
                if (next.getSymbolX() < x + gridSize*2) 
                    shiftSymbols(r, next.getSymbolX(), getWidth(), ((x - next.getSymbolX()) / gridSize) + 2);
                if (next.getSymbolX() > (x + gridSize*6)) 
                    shiftSymbols(r, next.getSymbolX(), getWidth(), (x - next.getSymbolX())/ gridSize + 6);
            }
        }
        // System.out.println(measureLocations);
    }

    private ArrayList<Component> getComponentsInXOrder() {
        ArrayList<Component> children = new ArrayList<Component>(Arrays.asList(getComponents()));
        
        children.sort(Comparator.comparing(Component::getSymbolX));
        int i = 0;
        while (i < children.size()) 
            else i++;
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
        int staffXStart = x_staffBuffer;
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
                    g2.drawLine(s.getX() + 3, y, s.getX() + symbolSize / 5, y);
                }
            }
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

    public Track getMidiTrack(Sequence sequence) {
        Track track = sequence.createTrack();
        // ArrayList<Component> sorted = getComponentsInXOrder();
        int cur_tick = 0;
        int channel = 0;
        int velocity = 100;
        for (ArrayList<Symbol> row : rows) {
            for (Symbol s : row) {
                // symbol is a note or rest
                if (s.sym.noteDuration != 0) cur_tick += Math.abs(s.sym.noteDuration);
                // symbol is a note 
                if (s.sym.noteDuration > 0) {
                    int pitch = getNotePitch(s);
                    MidiPlayer.addNote(track, channel, pitch, 
                    velocity, s.sym.noteDuration, cur_tick);
                }
            }
        }
        
        return track;
    } 

    public int getNotePitch(Symbol s) {
        // get pitch from notes y location
        int vertical_position = (s.getY() / gridSize) % (gridHeight * gridSize);
        // the center of the staff should be C = 60
        int distance_from_center = (vertical_position - gridHeight / 2);
        return 60 + distance_from_center;
    }

    public void setActiveNote(MusicSymbol note) {
        this.activeMusicSymbol = note;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a JFrame
            JFrame frame = new JFrame("Draggable Container");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Create a draggable container
            TrackGUI trackPanel = new TrackGUI();
            
            // Create a MenuBar
            MenuBar menuBar = new MenuBar(trackPanel);
            ToolBar toolBar = new ToolBar(trackPanel);
            
            // Create a JScrollPane to add the draggable container with scrollbars
            JScrollPane scrollPane = new JScrollPane();

            // get size of the current user screen 
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int maxWidth = (int) (gd.getDisplayMode().getWidth() * 0.65);
            frame.setSize(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
            Dimension trackDimension = new Dimension((int) (maxWidth * 1.29), maxWidth);
            System.out.println("dim: " + trackDimension);
            trackPanel.setMaximumSize(trackDimension);
            trackPanel.setMinimumSize(trackDimension);
            trackPanel.setPreferredSize(trackDimension);
            scrollPane.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    // Repaint the content panel when resized
                    scrollPane.repaint();
                    trackPanel.repaint();
                }
            });

            scrollPane.getViewport().add(trackPanel);

            // Add the MenuBar and scroll pane to the frame
            frame.add(menuBar, BorderLayout.NORTH);
            frame.add(toolBar, BorderLayout.WEST);
            frame.add(scrollPane, BorderLayout.CENTER);

            //BottomPanel bottomPanel = new BottomPanel();
            MidiPlayer player = new MidiPlayer();
            frame.add(player, BorderLayout.SOUTH);

            frame.setVisible(true);

            trackPanel.addTitlesComposer();
        });
    }
}
