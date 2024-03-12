package com.ecs160;

import javax.sound.midi.*;

public class MidiPlayer {

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
}

