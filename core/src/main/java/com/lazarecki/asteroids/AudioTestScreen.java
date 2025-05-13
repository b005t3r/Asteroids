package com.lazarecki.asteroids;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;
import com.lazarecki.asteroids.audio.Synthesizer;
import com.lazarecki.asteroids.audio.filters.Envelope;
import com.lazarecki.asteroids.audio.oscilators.Frequency;
import com.lazarecki.asteroids.audio.oscilators.Slider;
import com.lazarecki.asteroids.audio.oscilators.Vibrato;
import com.lazarecki.asteroids.audio.waveforms.Waveforms;

public class AudioTestScreen implements Screen {
    @Override
    public void show() {
        final int sampleRate = 44100;
        final int channelCount = 1;

        Array<ShortArray> sfx = new Array<>();
//        for(int i = 0; i < 5; ++i)
//            sfx.add(createLaserSfx(sampleRate, channelCount));

        for(int i = 0; i < 5; ++i)
            sfx.add(createEngineSfx(sampleRate, channelCount));

        AudioDevice device = Gdx.audio.newAudioDevice(sampleRate, channelCount == 1);

        for(ShortArray sample : sfx) {
            device.writeSamples(sample.items, 0, sample.size);

            try { Thread.sleep(500); } catch (InterruptedException e) { }
        }

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

    private ShortArray createLaserSfx(int sampleRate, int channelCount) {
        Frequency frequency = new Frequency(MathUtils.random(1700, 2400));
        Slider slider = new Slider(frequency, MathUtils.random(-7.5f, -10.5f));

        final float totalDuration = 0.2f;
        final float attack = 0.025f;
        final float decay = 0.125f;

        ShortArray sample = new Synthesizer(slider, Waveforms.sine, sampleRate, channelCount)
            .addFilter(
                new Envelope(0.0f)
                    .addStep(attack, 1.0f, Interpolation.pow2Out)
                    .addStep(totalDuration - decay, 1.0f, Interpolation.linear)
                    .addStep(totalDuration, 0.0f, Interpolation.pow2In)
            )
            .synthesize(0.0f, totalDuration, null);

        return sample;
    }

    private ShortArray createEngineSfx(int sampleRate, int channelCount) {
        Frequency frequency = new Frequency(MathUtils.random(70, 95));
        Vibrato vibrato = new Vibrato(frequency, 10.0f, 0.025f);

        final float totalDuration = 3.0f;
        final float attack = 0.125f;
        final float decay = 0.125f;

        ShortArray sample = new Synthesizer(vibrato, Waveforms.sine, sampleRate, channelCount)
            .addFilter(
                new Envelope(0.0f)
                    .addStep(attack, 1.0f, Interpolation.pow2Out)
                    .addStep(totalDuration - decay, 1.0f, Interpolation.linear)
                    .addStep(totalDuration, 0.0f, Interpolation.pow2In)
            )
            .synthesize(0.0f, totalDuration, null);

        return sample;
    }
}
