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
import java.util.ArrayList;

public class MidiPlayer extends JPanel {
    private Sequencer sequencer;
    private JButton playButton;
    private JSlider positionSlider;
    private TrackGUI trackPanel;
    private static int[] steps = new int[] {2,4,5,7,9,11,0};

    public MidiPlayer(TrackGUI trackPanel) {
        // Initialize sequencer
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        this.trackPanel = trackPanel;
        // Create GUI components
        playButton = new JButton("Play");
        playButton.addActionListener(new PlayButtonListener());

        positionSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0); // Slider for position, from 0 to 100
        positionSlider.addChangeListener(new PositionSliderListener());

        // Add components to the frame
        add(positionSlider);
        add(playButton);
    }

    
    public void buildAndPlaySequence() {
        try {
            // Obtain a Sequencer instance
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setTempoInBPM(120);

            // Create a sequence
            Sequence sequence = new Sequence(Sequence.PPQ, 4);

            // Create a track
            javax.sound.midi.Track track = sequence.createTrack();

            // Add some notes to the track (example: C major scale)
            int channel = 0;
            int velocity = 50;
            int cur_tick = 0;
            // Set the sequence to the sequencer and start playing
            for (ArrayList<Symbol> row : this.trackPanel.getRows()) {
                for (Symbol note : row) {
                    System.out.println(note.getX() + " " + note.getY());
                    //Interesting bug for whole notes, the offset of y position is 60 pixels for all lines
                    int pitch = getNotePitch(note);
                    int noteDuration = note.sym.getDuration();
                    // symbol is a note or rest
                    if (noteDuration < 0) {
                        cur_tick += Math.abs(noteDuration);
                    }
                    else if (noteDuration > 0) {
                        addNote(track, channel, pitch, velocity, noteDuration, cur_tick);
                        cur_tick += Math.abs(noteDuration);
                    }
                }
            }
            sequencer.setSequence(sequence);
            sequencer.start();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public static int getNotePitch(Symbol s) {
        // get pitch from notes y location
        //C is at 60, 320, 580... gap between is 260 pixels
        //lowest note is 170, 430, 690.... (low F) (midi = 49)
        //highest note is 180, 440, 700... (high high C) = (midi = 60+14 = 74)
        int gridSize = 10;
        int gridHeight = 26;
        int vertical_position;
        if (s.sym == MusicSymbol.WHOLE)
            vertical_position = ((s.getY()-60) / gridSize) % (gridHeight * gridSize);
        else
            vertical_position = (s.getY() / gridSize) % (gridHeight * gridSize);
        int[] steps2 = new int[] {2,4,5,7,9,11,0};
        int[] steps3 = new int[] {2,4,5,7,9,11,12};
        int distance_from_center = ((-1 * (vertical_position - 6)) % 26);
        int pitch_from_mid_c = 0;
        if (distance_from_center > 0)
            pitch_from_mid_c = ((distance_from_center / 7) * 12) + steps2[(distance_from_center - 1) % 7];
        if (distance_from_center < 0)
            pitch_from_mid_c = ((distance_from_center / 7) * 12) - (12 - steps3[(steps3.length + (distance_from_center % 7) - 1) % 7]);
        System.out.println("vertical " + vertical_position + " distance " + distance_from_center + " pitch " + (12 - steps3[(steps3.length + (distance_from_center % 7) - 1) % 7]));
        if (s.getAccidental() == MusicSymbol.SHARP)
            pitch_from_mid_c += 1;
        if (s.getAccidental() == MusicSymbol.FLAT)
            pitch_from_mid_c -= 1;
        return 60 + pitch_from_mid_c;
    }

    public static void addNote(Track track, int channel, int pitch, int velocity, int duration, int start_tick) {
        try {
            //System.out.println(channel + " " + pitch + " " + velocity + " " + start_tick);
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
    
    class PlayButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (sequencer.isRunning()) {
                sequencer.stop();
                playButton.setText("Play");
            } else {
                buildAndPlaySequence();
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



