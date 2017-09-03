package com.blucifer.dragoncurve;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

	public static final int WIDTH = 1000;
	public static final int HEIGHT = 750;

	private int itterations;
	private float segmentLen;
	private Point2D startPoint;

	private boolean[] turns;
	// private int maxIterations;

	private int renderSpeed;
	private int renderIndex;
	private int renderFacing;
	private Point2D renderP1;
	private Point2D renderP2;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Dragon Curve");

		Group root = new Group();
		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		root.getChildren().add(canvas);

		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.setFont(Font.font(20));
		graphics.fillText("Rendering...", WIDTH / 2 - 60, HEIGHT / 2 - 20);

		primaryStage.setScene(new Scene(root));
		primaryStage.setResizable(false);
		primaryStage.show();

		itterations = 12;
		segmentLen = 6;
		startPoint = new Point2D(600, 400);
		
		renderSpeed = 3;

		calculate(itterations);
		// debug();
		drawTrace(graphics);
		draw(graphics);
	}

	//

	/*
	 * Primary: http://mathworld.wolfram.com/DragonCurve.html
	 * Alt: https://en.wikipedia.org/wiki/Dragon_curve
	 */
	private void calculate(int maxIterations) {
		int maxNumTurns = turnsPerIteration(maxIterations);
		turns = new boolean[maxNumTurns];

		turns[0] = true;
		int prevTurns = 1;

		for (int iter = 2; iter <= maxIterations; iter++) {
			turns[prevTurns] = true; // 0-indexed, don't need +1

			int flipMark = prevTurns / 2;
			for (int newIndex = 0; newIndex <= prevTurns - 1; newIndex++) {
				int copyToIndex = prevTurns + newIndex + 1;
				turns[copyToIndex] = (newIndex == flipMark ? (!turns[newIndex]) : (turns[newIndex]));
			}

			prevTurns = prevTurns * 2 + 1;
		}
	}

	private int turnsPerIteration(int iteration) {
		return ((int) Math.pow(2, iteration)) - 1;
	}

	private void drawTrace(GraphicsContext gc) {
		gc.clearRect(0, 0, 1000, 750);
		gc.setStroke(Color.LIGHTGRAY);

		int facing = 0; // RIGHT(0) UP(1) LEFT(2) DOWN(3)

		Point2D p1 = startPoint;
		Point2D p2 = getEndPoint(p1, facing);
		gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		p1 = p2;

		for (int i = 0; i <= turns.length - 1; i++) {
			facing = facing + (turns[i] ? 1 : -1);
			if (facing < 0) {
				facing += 4;
			} else {
				facing = facing % 4;
			}

			p2 = getEndPoint(p1, facing);

			gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());

			p1 = p2;
		}
	}

	private void draw(GraphicsContext gc) {
		// gc.clearRect(0, 0, 1000, 750);
		gc.setStroke(Color.BLACK);

		renderFacing = 0; // RIGHT(0) UP(1) LEFT(2) DOWN(3)

		renderP1 = startPoint;
		renderP2 = getEndPoint(renderP1, renderFacing);
		gc.strokeLine(renderP1.getX(), renderP1.getY(), renderP2.getX(), renderP2.getY());
		renderP1 = renderP2;

		renderIndex = 0;

		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				for (int i = 1; i <= renderSpeed; i++) {
					if (renderIndex > turns.length - 1) {
						return;
					}

					renderFacing = renderFacing + (turns[renderIndex] ? 1 : -1);
					if (renderFacing < 0) {
						renderFacing += 4;
					} else {
						renderFacing = renderFacing % 4;
					}

					renderP2 = getEndPoint(renderP1, renderFacing);

					gc.strokeLine(renderP1.getX(), renderP1.getY(), renderP2.getX(), renderP2.getY());

					renderP1 = renderP2;

					renderIndex++;
				}
			}
		};
		timer.start();

	}

	private Point2D getEndPoint(Point2D startingPoint, int direction) {
		// RIGHT(0) UP(1) LEFT(2) DOWN(3)
		Point2D endPoint = startingPoint;
		switch (direction) {
			case 0: // Right
				endPoint = startingPoint.add(segmentLen, 0);
				break;
			case 1: // Up
				endPoint = startingPoint.add(0, -segmentLen);
				break;
			case 2: // Left
				endPoint = startingPoint.add(-segmentLen, 0);
				break;
			case 3: // Down
				endPoint = startingPoint.add(0, segmentLen);
				break;
		}
		return endPoint;
	}

	@SuppressWarnings("unused")
	private void debug() {
		for (int i = 0; i <= turns.length - 1; i++) {
			System.out.print((turns[i] ? 1 : 0) + " ");
		}
		System.out.println();
		for (int i = 0; i <= turns.length - 1; i++) {
			System.out.print((turns[i] ? "R" : "L") + " ");
		}

	}

	//

	public static void main(String[] args) {
		launch(args);
	}
}
