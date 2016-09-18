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
  private int points = 0;
  ArrayList<Tile> buttonsList;
  ArrayList<Integer> idList;
  TextView pointsText;
  LinearLayout viewHolder;
  int selectedTile = -1;

  // Please note that when modifying these values, add/remove images equal to 2*(diff).
  int width = 4;
  int height = 5;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Bundle bundle;

    //(TODO) (nsaric) add functionality for loading data just like rotation.
    if (getIntent().getExtras() != null) {
      bundle = new Bundle();
      bundle.putInt("checked", (int) this.getIntent().getExtras().get("checked"));
      bundle.putInt("points", (int) this.getIntent().getExtras().get("points"));
      for (int i = 0; i < (int) this.getIntent().getExtras().get("height"); i++) {
        for (int j = 0; j < (int) this.getIntent().getExtras().get("width"); j++) {
          String key = "" + i + j;
          bundle.putInt(key, (int) this.getIntent().getExtras().get(key));
        }
      }
    } else {
      bundle = savedInstanceState;
    }

    super.onCreate(bundle);
    setContentView(R.layout.game_activity);

    if (buttonsList == null) {
      buttonsList = new ArrayList<>();
    }
    if (idList == null) {
      idList = new ArrayList<>();
    }

    pointsText = (TextView) findViewById(R.id.points_text);
    viewHolder = (LinearLayout) findViewById(R.id.view_holder);

    pointsText.setText(R.string.points + points);

    if (bundle != null && bundle.get("01") != null) {
      System.out.println("onCreate notNullState called.");
      idList = new ArrayList<>();
      selectedTile = (int) bundle.get("checked");
      points = (int) bundle.get("points");
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          String key = "" + i + j;
          idList.add((int) bundle.get(key));
        }
      }
    } else {
      generateButtons(bundle);
    }
  }

  public void generateButtons(Bundle inBundle) {
    viewHolder.removeAllViews();
    for (int i = 0; i < height; i++) {
      LinearLayout row = new LinearLayout(viewHolder.getContext());
      row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
      row.setOrientation(LinearLayout.HORIZONTAL);
      for (int j = 0; j < width; j++) {
        Tile button;
        if (inBundle == null || inBundle.get("01") == null) { // New buttons.
          int imageSource =
              (int) Math.round(Math.random()) == 0 ? R.drawable.image1 : R.drawable.image2;
          button = new Tile(viewHolder.getContext(), imageSource);
        } else { // Recreate buttons.
          int pos = (i * height - i) + (j);
          button = new Tile(getApplicationContext(), idList.get(pos), (
              idList.get((i * height - i) + j) == R.drawable.check
                  || (i * height - i) + j == selectedTile));
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
                    points++;
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
        buttonsList.add(button);
        idList.add(button.imageResource);
      }
      viewHolder.addView(row);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putInt("points", points);
    outState.putInt("checked", selectedTile);
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

    if (inBundle != null) {
      idList = new ArrayList<>();
      selectedTile = (int) inBundle.get("checked");
      points = (int) inBundle.get("points");
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          String key = "" + i + j;
          idList.add((int) inBundle.get(key));
        }
      }
    }
    generateButtons(inBundle);
  }

  @Override
  public void onBackPressed() {
    Intent main = new Intent(getApplicationContext(), MainActivity.class);
    main.putExtra("height", height);
    main.putExtra("width", width);
    main.putExtra("points", points);
    main.putExtra("checked", selectedTile);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        String key = "" + i + j;
        int pos = (i * height - i) + (j);
        main.putExtra(key, buttonsList.get(pos).imageResource);
      }
    }
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
