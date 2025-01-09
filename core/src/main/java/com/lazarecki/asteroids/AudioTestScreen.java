package com.lazarecki.asteroids;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.ShortArray;
import com.lazarecki.asteroids.audio.Synthesizer;
import com.lazarecki.asteroids.audio.filters.Envelope;
import com.lazarecki.asteroids.audio.filters.Volume;
import com.lazarecki.asteroids.audio.oscilators.Oscilators;

public class AudioTestScreen implements Screen {
    @Override
    public void show() {
        final int sampleRate = 44100;
        final int channelCount = 1;

        /*
        short[] samples = new short[44100 * 5];

        AudioRecorder recorder = Gdx.audio.newAudioRecorder(44100, true);
        recorder.read(samples, 0, samples.length);
*/


        ShortArray sample = new Synthesizer(Oscilators.sine, sampleRate, channelCount)
            .addFilter(
                new Envelope(0.0f)
                    .addStep(0.1f, 1.0f, Interpolation.pow2In)
                    .addStep(1.8f, 1.0f, Interpolation.linear)
                    .addStep(2.0f, 0.0f, Interpolation.pow2Out)
            )
            .addFilter(new Volume(0.9f))
            .synthesize(55, 0.0f, 2.0f, null);

        AudioDevice device = Gdx.audio.newAudioDevice(sampleRate, channelCount == 1);
        device.writeSamples(sample.items, 0, sample.size);

        Gdx.app.exit();
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
