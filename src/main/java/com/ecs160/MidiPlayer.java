package com.ecs160;

import javax.sound.midi.*;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MidiPlayer extends JPanel {
    private Sequencer sequencer;
    private JButton playButton;
    private JSlider positionSlider;
    private TrackGUI trackPanel;

    public MidiPlayer(TrackGUI trackPanel) {
        // Initialize sequencer
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setLoopCount(0);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        this.trackPanel = trackPanel;
        // Create GUI components
        playButton = new JButton("Play");
        playButton.addActionListener(new PlayButtonListener());

        positionSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0); // Slider for position, from 0 to 100

        // Add components to the frame
        add(positionSlider);
        add(playButton);
    }

    
    public Sequence buildSequence(TrackGUI trackpanel) {
        try {
            // Obtain a Sequencer instance
            sequencer.open();
            sequencer.setTempoInBPM(trackpanel.getTempo());

            // Create a sequence
            Sequence sequence = new Sequence(Sequence.PPQ, 4);

            // Create a track
            javax.sound.midi.Track track = sequence.createTrack();

            // Add some notes to the track (example: C major scale)
            int channel = 0;
            int velocity = 50;
            int cur_tick = 0;
            // go through all notes in row order 
            for (ArrayList<Symbol> row : this.trackPanel.getRows()) {
                for (Symbol note : row) {
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
            return sequence;
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getNotePitch(Symbol s) {
        int gridSize = 10;
        int gridHeight = 26;
        int rowHeight = gridHeight * gridSize;
        int c_pitch = 60;
        int vertical_position;
        
        if (s.sym == MusicSymbol.WHOLE)
            vertical_position = ((s.getBottomY())) % (rowHeight) / gridSize;
        else
            vertical_position = (s.getBottomY()) % (rowHeight) / gridSize;
        
        // System.out.println(vertical_position);
        int[] steps2 = {2,4,5,7,9,11,0};
        int[] steps3 = {2,4,5,7,9,11,12};
        
        int distance_from_center = ((-1 * (vertical_position - 6)) % gridHeight);
        
        int pitch_from_mid_c = 0;
        if (distance_from_center > 0)
            pitch_from_mid_c = ((distance_from_center / 7) * 12) + steps2[(distance_from_center - 1) % 7];
        if (distance_from_center < 0)
            pitch_from_mid_c = ((distance_from_center / 7) * 12) - (12 - steps3[(steps3.length + (distance_from_center % 7) - 1) % 7]);
        
        if (s.getAccidental() == MusicSymbol.SHARP)
            pitch_from_mid_c += 1;
        if (s.getAccidental() == MusicSymbol.FLAT)
            pitch_from_mid_c -= 1;
        
        return c_pitch + pitch_from_mid_c;
    }

    public static void addNote(Track track, int channel, int pitch, int velocity, int duration, int start_tick) {
        try {
            track.add(createNoteOnEvent(channel, pitch, velocity, start_tick));
            track.add(createNoteOffEvent(channel, pitch, start_tick + duration));
        } catch (InvalidMidiDataException e) {
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
       
    class PlayButtonListener implements ActionListener, MetaEventListener {
        private Thread positionUpdater;
    
        @Override
        public void actionPerformed(ActionEvent e) {
            // if the user pauses while the music is playing
            if (sequencer.isRunning()) {
                sequencer.stop();
                playButton.setText("Play");
                if (positionUpdater != null) {
                    positionUpdater.interrupt();
                }
            } else {
                // check if track has changed
                    Sequence new_sequence = buildSequence(trackPanel);
                    try {
                        sequencer.setSequence(new_sequence);
                    } catch (InvalidMidiDataException e1) {
                        e1.printStackTrace();
                    }
                    if (sequencer.getSequence() != null) {
                        long position = (long) (sequencer.getMicrosecondLength() * (positionSlider.getValue() / 100.0));
                        sequencer.setMicrosecondPosition(position);
                    } 
                // start playing the sequence / music
                sequencer.addMetaEventListener(this); // Add MetaEventListener
                sequencer.start();
                playButton.setText("Pause");
                // start thread that adjusts value of the slider
                positionUpdater = new Thread(new UpdatePositionTask());
                positionUpdater.start();
            }
        }

    // Implement MetaEventListener to check when the end of sequence is reached
    @Override
    public void meta(MetaMessage meta) {
        if (meta.getType() == 47) { // End of track event
            // Reset play button, slider, and sequence
            playButton.setText("Play");
            sequencer.stop();
            sequencer.setMicrosecondPosition(0);
            positionSlider.setValue(0);
            sequencer.removeMetaEventListener(this); // Remove MetaEventListener
        }
    }
    }
        
    // changes the value of the playback slider via threading
    class UpdatePositionTask implements Runnable {
        @Override
        public void run() {
            // check if the sequence is playing music
            while (sequencer.isRunning()) {
                // change position of slider based on position in the sequence
                long position = sequencer.getMicrosecondPosition();
                long length = sequencer.getMicrosecondLength();
                if (length > 0) {
                    int value = (int) (position * 100 / length);
                    positionSlider.setValue(value);
                }
                try {
                    Thread.sleep(30); // Update every 100 milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}