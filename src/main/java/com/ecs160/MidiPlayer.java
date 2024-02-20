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
            int velocity = 100;
            int noteDuration = 4; // quarter note duration in ticks

            for (int i = 60; i <= 72; i += 3) { // C major scale from middle C (MIDI note 60)
                track.add(createNoteOnEvent(channel, i, velocity, 0)); // Note on
                track.add(createNoteOffEvent(channel, i, 0 + noteDuration)); // Note off
            }

            // Set the sequence to the sequencer and start playing
            sequencer.setSequence(sequence);
            sequencer.start();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
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

