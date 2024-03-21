package com.ecs160;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a JFrame to be the top level window
            JFrame frame = new JFrame("Mooscore V 1.0");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ImageIcon icon = new ImageIcon("src\\main\\java\\com\\ecs160\\imgs\\mooscore logo.png"); // Replace with the path to your icon file
            frame.setIconImage(icon.getImage());
            
            // Create a draggable container
            TrackGUI trackPanel = new TrackGUI();
            
            // Create a MenuBar
            MenuBar menuBar = new MenuBar(trackPanel);
            ToolBar toolBar = new ToolBar(trackPanel);

            // Create a JScrollPane to add the draggable container with scrollbars
            JScrollPane scrollPane = new JScrollPane();

            // get size of the current user screen 
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int maxWidth = Math.min(1500, (int) (gd.getDisplayMode().getWidth() * 0.65));
            
            // make window full screen 
            frame.setSize(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
            Dimension trackDimension = new Dimension((int) (maxWidth * 1.29), maxWidth);
            // System.out.println("dim: " + trackDimension);
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

            MidiPlayer player = new MidiPlayer(trackPanel);
            frame.add(player, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }

}