package D.HollowKnight.views;

import D.HollowKnight.controllers.MenuController;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SettingsTable extends Table {

    private boolean isMusicOn;
    private boolean isSfxOn;
    private String currentLang;
    private Table controlsTable;

    public SettingsTable(TextButton.TextButtonStyle btnStyle, Table mainTable, Image settingsBg, MenuController controller, Image darknessLayer) {
        this.setFillParent(true);
        this.center().padTop(100);
        this.setPosition(1280, 0);

        isMusicOn = controller.isMusicOn();
        isSfxOn = controller.isSfxOn();
        currentLang = controller.getLanguage();

        Pixmap bgPixmap = new Pixmap(300, 10, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(Color.DARK_GRAY); bgPixmap.fill();
        TextureRegionDrawable sliderBg = new TextureRegionDrawable(new Texture(bgPixmap)); bgPixmap.dispose();

        Pixmap knobPixmap = new Pixmap(20, 30, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(Color.WHITE); knobPixmap.fill();
        TextureRegionDrawable sliderKnob = new TextureRegionDrawable(new Texture(knobPixmap)); knobPixmap.dispose();

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle(sliderBg, sliderKnob);
        Label.LabelStyle labelStyle = new Label.LabelStyle(btnStyle.font, Color.WHITE);

        Label volumeLabel = new Label("MUSIC VOLUME : " + controller.getVolume() + "%", labelStyle);
        Slider volumeSlider = new Slider(0, 100, 1, false, sliderStyle);
        volumeSlider.setValue(controller.getVolume());

        TextButton toggleMusicBtn = new TextButton("MUSIC : " + (isMusicOn ? "ON" : "OFF"), btnStyle);
        TextButton toggleSfxBtn = new TextButton("SFX : " + (isSfxOn ? "ON" : "OFF"), btnStyle);
        TextButton resetSoundsBtn = new TextButton("RESET SOUNDS", btnStyle);
        TextButton controlsBtn = new TextButton("CHANGE CONTROLS", btnStyle);
        TextButton resetControlsBtn = new TextButton("RESET CONTROLS", btnStyle);

        Label brightnessLabel = new Label("BRIGHTNESS : " + controller.getBrightness() + "%", labelStyle);
        Slider brightnessSlider = new Slider(20, 100, 1, false, sliderStyle);
        brightnessSlider.setValue(controller.getBrightness());

        TextButton langBtn = new TextButton("LANGUAGE : " + currentLang, btnStyle);
        TextButton backBtn = new TextButton("BACK", btnStyle);

        this.add(volumeLabel).padBottom(5).padLeft(45).row();
        this.add(volumeSlider).width(300).padBottom(20).padLeft(45).row();
        this.add(toggleMusicBtn).padBottom(10).padLeft(45).row();
        this.add(toggleSfxBtn).padBottom(10).padLeft(45).row();
        this.add(resetSoundsBtn).padBottom(20).padLeft(45).row();
        this.add(controlsBtn).padBottom(10).padLeft(45).row();
        this.add(resetControlsBtn).padBottom(20).padLeft(45).row();
        this.add(brightnessLabel).padBottom(5).padLeft(45).row();
        this.add(brightnessSlider).width(300).padBottom(20).padLeft(45).row();
        this.add(langBtn).padBottom(20).padLeft(45).row();
        this.add(backBtn).padLeft(45).row();

        volumeSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                int val = (int) volumeSlider.getValue();
                volumeLabel.setText("MUSIC VOLUME : " + val + "%");
                controller.setVolume(val);
            }
        });

        brightnessSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                int val = (int) brightnessSlider.getValue();
                brightnessLabel.setText("BRIGHTNESS : " + val + "%");
                controller.setBrightness(val);
                darknessLayer.getColor().a = 1f - (val / 100f);
            }
        });

        toggleMusicBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                isMusicOn = !isMusicOn;
                toggleMusicBtn.setText(isMusicOn ? "MUSIC : ON" : "MUSIC : OFF");
                controller.setMusicOn(isMusicOn);
            }
        });

        toggleSfxBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                isSfxOn = !isSfxOn;
                toggleSfxBtn.setText(isSfxOn ? "SFX : ON" : "SFX : OFF");
                controller.setSfxOn(isSfxOn);
            }
        });

        resetSoundsBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                controller.resetSounds();
                volumeSlider.setValue(100);
                isMusicOn = true; isSfxOn = true;
                toggleMusicBtn.setText("MUSIC : ON");
                toggleSfxBtn.setText("SFX : ON");
            }
        });

        langBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                currentLang = currentLang.equals("ENGLISH") ? "PERSIAN" : "ENGLISH";
                langBtn.setText("LANGUAGE : " + currentLang);
                controller.setLanguage(currentLang);
            }
        });

        controlsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsTable.this.addAction(Actions.moveTo(-1280, 0, 0.5f, Interpolation.exp10Out));
                controlsTable.addAction(Actions.moveTo(0, 0, 0.5f, Interpolation.exp10Out));
            }
        });

        resetControlsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.resetControls();
                ((ControlsTable) controlsTable).refreshLabels();
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                SettingsTable.this.addAction(Actions.moveTo(1280, 0, 0.5f, Interpolation.exp10Out));
                mainTable.addAction(Actions.moveTo(0, 0, 0.5f, Interpolation.exp10Out));
                settingsBg.addAction(Actions.fadeOut(0.5f));
            }
        });
    }

    public void setControlsTable(Table controlsTable) {
        this.controlsTable = controlsTable;
    }
}
