package com.example.tendero_mp1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private TextView tvTimer;
    private GridLayout gridLayout;

    // Game Variables
    private ArrayList<Integer> allCardImages;
    private List<Integer> currentLevelCardImages;
    private ArrayList<ImageView> cards;
    private int firstCard = -1, secondCard = -1;
    private int matchedPairs = 0;
    private boolean isChecking = false;

    // Level Management
    private int currentLevel = 1;
    private static final int MAX_LEVEL = 3;

    // Timer
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTimer = findViewById(R.id.tvTimer);
        gridLayout = findViewById(R.id.gridLayout);
        Button btnRestart = findViewById(R.id.btnRestart);

        btnRestart.setOnClickListener(v -> restartGame());

        loadAllCardImages();
        setupGame();
    }

    private void loadAllCardImages() {
        allCardImages = new ArrayList<>();
        allCardImages.add(R.drawable.ic_stardew_leah);
        allCardImages.add(R.drawable.ic_stardew_alex);
        allCardImages.add(R.drawable.ic_stardew_elliot);
        allCardImages.add(R.drawable.ic_stardew_sebastian);
        allCardImages.add(R.drawable.ic_stardew_penny);
        allCardImages.add(R.drawable.ic_stardew_sam);
        allCardImages.add(R.drawable.ic_stardew_haley);
        allCardImages.add(R.drawable.ic_stardew_abigail);
    }

    private void setupGame() {
        int gridColumnCount;
        int gridRowCount;
        int numPairs;

        // --- MODIFIED: Level configurations updated to your new request ---
        switch (currentLevel) {
            case 2:
                // Level 2 (Normal): 12 cards (6 pairs) in a 3x4 grid
                gridColumnCount = 3;
                gridRowCount = 4;
                numPairs = 6;
                timeLeftInMillis = 60000; // 60 seconds
                break;
            case 3:
                // Level 3 (Hard): 16 cards (8 pairs) in a 4x4 grid
                gridColumnCount = 4;
                gridRowCount = 4;
                numPairs = 8;
                timeLeftInMillis = 80000; // 80 seconds
                break;
            default: // case 1
                // Level 1 (Easy): 10 cards (5 pairs) in a 2x5 grid
                gridColumnCount = 2;
                gridRowCount = 4;
                numPairs = 4;
                timeLeftInMillis = 50000; // 50 seconds
                break;
        }

        // Reset game state
        matchedPairs = 0;
        firstCard = -1;
        secondCard = -1;
        isChecking = false;
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(gridColumnCount);
        gridLayout.setRowCount(gridRowCount);

        // REPLACE IT WITH THIS
// Prepare the list of images for the current level
        Collections.shuffle(allCardImages);
        List<Integer> singleCards = allCardImages.subList(0, numPairs); // Create a temporary list of single cards
        currentLevelCardImages = new ArrayList<>(singleCards); // Add the single cards once
        currentLevelCardImages.addAll(singleCards); // Add them a second time to create pairs
        Collections.shuffle(currentLevelCardImages); // Now shuffle the complete list of pairs

        // Initialize and add cards to the grid
        cards = new ArrayList<>();
        for (int i = 0; i < gridColumnCount * gridRowCount; i++) {
            ImageView card = new ImageView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            card.setLayoutParams(params);
            card.setImageResource(R.drawable.ic_card_back);
            card.setTag(i);
            card.setOnClickListener(cardClickListener);
            cards.add(card);
            gridLayout.addView(card);
        }

        startTimer();
    }

    private final View.OnClickListener cardClickListener = v -> {
        if (isChecking) return;
        ImageView clickedCard = (ImageView) v;
        int position = (int) clickedCard.getTag();
        if (clickedCard.getAlpha() == 0.5f || position == firstCard) return;
        flipCardAnimation(clickedCard, currentLevelCardImages.get(position));
        if (firstCard == -1) {
            firstCard = position;
        } else {
            secondCard = position;
            isChecking = true;
            checkForMatch();
        }
    };
    // Add this entire new method to MainActivity.java

    private void flipCardAnimation(final ImageView card, final int newImageResource) {
        // This is a handler to delay the second part of the animation
        final Handler handler = new Handler();

        // The first part of the animation: flipping the card out
        final Runnable flipOut = () -> {
            // Set the camera distance for a 3D effect
            float scale = getApplicationContext().getResources().getDisplayMetrics().density;
            card.setCameraDistance(8000 * scale);

            ObjectAnimator animator = ObjectAnimator.ofFloat(card, "rotationY", 0f, 90f);
            animator.setDuration(250); // Animation duration in milliseconds
            animator.start();
        };

        // The second part of the animation: flipping the card back in with the new image
        final Runnable flipIn = () -> {
            card.setImageResource(newImageResource);

            ObjectAnimator animator = ObjectAnimator.ofFloat(card, "rotationY", -90f, 0f);
            animator.setDuration(250);
            animator.start();
        };

        // Execute the first part of the animation
        flipOut.run();

        // Post the second part of the animation with a delay that matches the first animation's duration
        handler.postDelayed(flipIn, 250);
    }

    private void checkForMatch() {
        new Handler().postDelayed(() -> {
            if (currentLevelCardImages.get(firstCard).equals(currentLevelCardImages.get(secondCard))) {
                cards.get(firstCard).setAlpha(0.5f);
                cards.get(secondCard).setAlpha(0.5f);
                cards.get(firstCard).setOnClickListener(null);
                cards.get(secondCard).setOnClickListener(null);
                matchedPairs++;
                if (matchedPairs == currentLevelCardImages.size() / 2) {
                    gameWon();
                }
            } else {
                flipCardAnimation(cards.get(firstCard), R.drawable.ic_card_back);
                flipCardAnimation(cards.get(secondCard), R.drawable.ic_card_back);
            }
            firstCard = -1;
            secondCard = -1;
            isChecking = false;
        }, 1000);
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }
            @Override
            public void onFinish() {
                gameOver();
            }
        }.start();
    }

    private void updateTimerText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        tvTimer.setText(String.format(Locale.ROOT, "Level: %d | Time: %ds", currentLevel, seconds));
    }

    private void gameWon() {
        countDownTimer.cancel();
        if (currentLevel < MAX_LEVEL) {
            new AlertDialog.Builder(this)
                    .setTitle("Level " + currentLevel + " Complete!")
                    .setMessage("Ready for the next level?")
                    .setPositiveButton("Next Level", (dialog, which) -> {
                        currentLevel++;
                        setupGame();
                    })
                    .setNegativeButton("Exit", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Congratulations!")
                    .setMessage("You have completed all levels!")
                    .setPositiveButton("Play Again", (dialog, which) -> restartGame())
                    .setNegativeButton("Exit", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
        }
    }

    private void gameOver() {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Time's up! You lose.")
                .setPositiveButton("Try Again", (dialog, which) -> {
                    setupGame();
                })
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void restartGame() {
        currentLevel = 1;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        setupGame();
        Toast.makeText(this, "Game Restarted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}