package cs175homeworks.memorygame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
  private Integer points;
  ArrayList<Tile> buttonsList;
  ArrayList<Integer> picturesList;
  TextView pointsText;
  LinearLayout viewHolder;
  int selectedTile;
  int flipped;

  // Please note that when modifying these values, add/remove images equal to 2*(diff).
  int width = 4;
  int height = 5;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Bundle bundle;

    boolean reload = (this.getIntent().getExtras() != null);
    System.out.println("Extras != null: " + reload);
    if (reload) {
      reload = this.getIntent().getExtras().get("restart") != null;
      System.out.println("restart != null: " + reload);
      if (reload) {
        reload = !((boolean) this.getIntent().getExtras().get("restart"));
        System.out.println("restart: " + !reload);
      }
    }

    if (savedInstanceState != null) { // Check both Extras and savedInstanceState.
      System.out.println("savedInstanceState != null");
      reload = true;
    }

    if (reload) {
      if (savedInstanceState != null) {
        bundle = savedInstanceState;
      } else {
        bundle = this.getIntent().getExtras();
      }
      buttonsList = new ArrayList<>();
      selectedTile = (int) bundle.get("checked");
      flipped = (int) bundle.get("flipped");
      points = (int) bundle.get("points");
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          String key = "" + i + j;
          System.out.print((i * height - i + j) + " ");
          buttonsList.add(new Tile(getApplicationContext(), (int) bundle.get(key), (
              (int) bundle.get(key) == R.drawable.check
                  || (i * height - i) + j == selectedTile)));
        }
      }
    } else {
      bundle = null;
      buttonsList = new ArrayList<>();
      selectedTile = -1;
      points = 0;
      flipped = 0;
      picturesList = new ArrayList<>();
      for (int i = 0; i < (width * height)/2; i++) {
        int id = getResources().getIdentifier(
            "image" + i, "drawable", getApplicationContext().getPackageName());
        picturesList.add(id);
        picturesList.add(id);
      }
    }

    super.onCreate(bundle);
    setContentView(R.layout.game_activity);

    pointsText = (TextView) findViewById(R.id.points_text);
    viewHolder = (LinearLayout) findViewById(R.id.view_holder);
    String initialPoints = getString(R.string.points) + points;
    pointsText.setText(initialPoints);

    System.out.println("!reload: " + !reload);
    generateButtons(!reload);
  }

  public void generateButtons(boolean newButtons) {
    viewHolder.removeAllViews();
    for (int i = 0; i < height; i++) {
      LinearLayout row = new LinearLayout(viewHolder.getContext());
      row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
      row.setOrientation(LinearLayout.HORIZONTAL);
      for (int j = 0; j < width; j++) {
        Tile button;
        if (newButtons) { // New buttons.
          int imageSource = randomPicture();
          button = new Tile(viewHolder.getContext(), imageSource);
        } else { // Recreate buttons.
          int pos = (i * height - i) + j;
          button = new Tile(getApplicationContext(), buttonsList.get(pos).imageResource, (
              buttonsList.get(pos).imageResource == R.drawable.check
                  || pos == selectedTile));
          if (pos == selectedTile) {
            button.setBackgroundResource(button.imageResource);
          }
        }
        button.setScaleType(ImageView.ScaleType.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        params.setMargins(4, 4, 4, 4);
        button.setLayoutParams(params);
        button.setPadding(R.dimen.button_padding, R.dimen.button_padding,
            R.dimen.button_padding, R.dimen.button_padding);

        if (button.imageResource != R.drawable.check) {
          button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (v instanceof Tile) {
                if (selectedTile >= 0 && v.equals(buttonsList.get(selectedTile))) { // Same tile.
                  return;
                }

                if (selectedTile >= 0) {
                  Tile flippedTile = buttonsList.get(selectedTile);
                  ((Tile) v).flip(flippedTile);
                  if (((Tile) v).imageResource == R.drawable.check) {
                    flipped += 2;
                    points++;
                    if (flipped == (width * height)) {
                      Intent victory = new Intent(getApplicationContext(), VictoryActivity.class);
                      victory.putExtra("points", points);
                      startActivity(victory);
                      finish();
                    }
                    selectedTile = -1;
                  } else {
                    points--;
                    selectedTile = -1;
                  }
                } else {
                  selectedTile = buttonsList.indexOf(v);
                  ((Tile) v).flip(null);
                }

                String text = getString(R.string.points) + points;
                pointsText.setText(text);
              }
            }
          });
        } else {
          button.setOnClickListener(null);
        }
        row.addView(button);
        if (!newButtons) {
          buttonsList.set((i * height - i) + j, button);
        } else {
          buttonsList.add(button);
        }
      }
      viewHolder.addView(row);
    }
  }

  public int randomPicture() {
    int position = (int) (Math.random() * picturesList.size());
    int pictureId = picturesList.get(position);
    picturesList.remove(position);
    return pictureId;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putInt("points", points);
    outState.putInt("checked", selectedTile);
    outState.putInt("flipped", flipped);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        String key = "" + i + j;
        int pos = (i * height - i) + (j);
        outState.putInt(key, buttonsList.get(pos).imageResource);
      }
    }
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onRestoreInstanceState(Bundle inBundle) {
    super.onRestoreInstanceState(inBundle);

    if (inBundle != null && buttonsList != null) {
      selectedTile = (int) inBundle.get("checked");
      points = (int) inBundle.get("points");
      flipped = (int) inBundle.get("flipped");
      buttonsList.clear();
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          String key = "" + i + j;
          buttonsList.add(new Tile(getApplicationContext(), (int) inBundle.get(key), (
              (int) inBundle.get(key) == R.drawable.check
                  || (i * height - i) + j == selectedTile)));
        }
      }
      generateButtons(false);
    }
  }

  @Override
  public void onBackPressed() {
    Intent main = new Intent(getApplicationContext(), MainActivity.class);
    main.putExtra("height", height);
    main.putExtra("width", width);
    main.putExtra("points", points);
    main.putExtra("checked", selectedTile);
    main.putExtra("flipped", flipped);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        String key = "" + i + j;
        int pos = (i * height - i) + (j);
        main.putExtra(key, buttonsList.get(pos).imageResource);
      }
    }
    startActivity(main);
    super.onBackPressed();
  }

  @SuppressLint("ViewConstructor")
  public static final class Tile extends ImageButton {
    int imageResource;

    public Tile(Context context, int imageResource) {
      super(context);
      this.imageResource = imageResource;
      this.setBackgroundResource(R.drawable.tile);
    }

    public Tile(Context context, int otherTile, boolean selected) {
      super(context);
      this.imageResource = otherTile;
      if (selected) {
        this.setBackgroundResource(imageResource);
      } else {
        this.setBackgroundResource(R.drawable.tile);
      }
    }

    public void flip(final Tile otherTile) {
      setBackgroundResource(imageResource);
      if (otherTile != null && this != otherTile) { // Second Tile Selection
        if (imageResource == otherTile.imageResource) { // Matching
          otherTile.imageResource = R.drawable.check;
          otherTile.setBackgroundResource(R.drawable.check);
          otherTile.setOnClickListener(null);

          imageResource = R.drawable.check;
          setBackgroundResource(R.drawable.check);
          setOnClickListener(null);
        } else { // Not matching.
          post(new Runnable() {
            @Override
            public void run() {
              try {
                Thread.sleep(1000);
              } catch (InterruptedException ignored) { }
              setBackgroundResource(R.drawable.tile);
              otherTile.setBackgroundResource(R.drawable.tile);
            }
          });
        }
      }
    }
  }
}
