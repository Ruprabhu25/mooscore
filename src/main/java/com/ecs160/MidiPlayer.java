package com.ecs160;

import javax.sound.midi.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MidiPlayer extends JFrame {
    private Sequencer sequencer;
    private JButton playButton;
    private JSlider positionSlider;

    public MidiPlayer() {
        super("MIDI Player");

        // Initialize sequencer
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        // Create GUI components
        playButton = new JButton("Play");
        playButton.addActionListener(new PlayButtonListener());

        positionSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0); // Slider for position, from 0 to 100
        positionSlider.addChangeListener(new PositionSliderListener());

        // Add components to the frame
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(playButton, BorderLayout.NORTH);
        controlPanel.add(positionSlider, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.CENTER);

        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    
    public static void buildAndPlaySequence() {
        try {
            // Obtain a Sequencer instance
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();

            // Create a sequence
            Sequence sequence = new Sequence(Sequence.PPQ, 4);

            // Create a track
            javax.sound.midi.Track track = sequence.createTrack();

            // Add some notes to the track (example: C major scale)
            int channel = 0;
            int velocity = 50;
            int noteDuration = 4; // quarter note duration in ticks

            for (int i = 60; i <= 72; i += 1) { // C major scale from middle C (MIDI note 60)
                addNote(track, channel, i, velocity + i * 10, noteDuration, i * 4 * 2);
            }
            // Set the sequence to the sequencer and start playing
            sequencer.setSequence(sequence);
            sequencer.start();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public static void addNote(Track track, int channel, int pitch, int velocity, int duration, int start_tick) {
        try {
            track.add(createNoteOnEvent(channel, pitch, velocity, start_tick));
            track.add(createNoteOffEvent(channel, pitch, start_tick + duration));
        } catch (InvalidMidiDataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static MidiEvent createNoteOnEvent(int channel, int note, int velocity, long tick) throws InvalidMidiDataException {
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.NOTE_ON, channel, note, velocity);
        return new MidiEvent(message, tick);
    }

    private static MidiEvent createNoteOffEvent(int channel, int note, long tick) throws InvalidMidiDataException {
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.NOTE_OFF, channel, note, 0);
        return new MidiEvent(message, tick);
    }

    public static void main(String[] args) {
        buildAndPlaySequence();
    }
    
    class PlayButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (sequencer.isRunning()) {
                sequencer.stop();
                playButton.setText("Play");
            } else {
                try {
                    sequencer.setSequence(/* Provide your MIDI sequence here */);
                    sequencer.start();
                    playButton.setText("Stop");
                } catch (InvalidMidiDataException | MidiUnavailableException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class PositionSliderListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (!positionSlider.getValueIsAdjusting()) {
                long position = (long) (sequencer.getMicrosecondLength() * (positionSlider.getValue() / 100.0));
                sequencer.setMicrosecondPosition(position);
            }
        }
    }
}



