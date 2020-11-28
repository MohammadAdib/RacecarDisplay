package mohammad.adib.racecar.ui;/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A label, that can be rotated by 90 degrees either left or right. Thus the
 * reading direction is either horizontal (the default), up or down.
 *
 * @author Johannes RÃ¶ssel
 */
public class VerticalLabel extends JLabel {

    /** Serialisation ID. */
    private static final long serialVersionUID = -5589305224172247331L;

    /** Represents the direction of a rotated label. */
    public enum Direction {
        /** Normal horizontal direction. */
        HORIZONTAL,
        /** Vertical, upwards (for left-to-right languages). */
        VERTICAL_UP,
        /** Vertical, downwards (for left-to-right languages). */
        VERTICAL_DOWN
    }

    /** The text direction. */
    private Direction direction;

    {
        // it's better to set this here as default for all constructors since they
        // only call the super constructors.
        setDirection(Direction.HORIZONTAL);
    }

    /**
     * A flag indicating whether {link #getSize()} and such methods need to return
     * a rotated dimension.
     */
    private boolean needsRotate;

    /**
     * Initialises a new instance of the {@link VerticalLabel} class using default
     * values.
     */
    public VerticalLabel() {
        super();
    }

    /**
     * Initialises a new instance of the {@link VerticalLabel} class using the
     * specified icon and horizontal alignment.
     *
     * @param image
     *          The icon to use for this label.
     * @param horizontalAlignment
     *          The horizontal alignment of the text.
     */
    public VerticalLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    /**
     * Initialises a new instance of the {@link VerticalLabel} class using the
     * specified icon.
     *
     * @param image
     *          The icon to use for this label.
     */
    public VerticalLabel(Icon image) {
        super(image);
    }

    /**
     * Initialises a new instance of the {@link VerticalLabel} class using the
     * specified text, icon and horizontal alignment.
     *
     * @param text
     *          The text to display.
     * @param icon
     *          The icon to use for this label.
     * @param horizontalAlignment
     *          The horizontal alignment of the text.
     */
    public VerticalLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    /**
     * Initialises a new instance of the {@link VerticalLabel} class using the
     * specified text and horizontal alignment.
     *
     * @param text
     *          The text to display.
     * @param horizontalAlignment
     *          The horizontal alignment of the text.
     */
    public VerticalLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    /**
     * Initialises a new instance of the {@link VerticalLabel} class using the
     * specified text.
     *
     * @param text
     *          The text to display.
     */
    public VerticalLabel(String text) {
        super(text);
    }

    /**
     * Gets the text direction of this {@link VerticalLabel}.
     *
     * @return The current text direction.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Sets the text direction of this {@link VerticalLabel}.
     *
     * @param direction
     *          The new direction.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();

        // swap size for vertical alignments
        switch (getDirection()) {
            case VERTICAL_UP: // NOTE: fall-through
            case VERTICAL_DOWN:
                return new Dimension(preferredSize.height, preferredSize.width);
            default:
                return preferredSize;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.Component#getSize()
     */
    @Override
    public Dimension getSize() {
        if (!needsRotate) {
            return super.getSize();
        }

        Dimension size = super.getSize();

        switch (getDirection()) {
            case VERTICAL_DOWN:
            case VERTICAL_UP:
                return new Dimension(size.height, size.width);
            default:
                return super.getSize();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#getHeight()
     */
    @Override
    public int getHeight() {
        return getSize().height;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#getWidth()
     */
    @Override
    public int getWidth() {
        return getSize().width;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D gr = (Graphics2D) g.create();

        switch (getDirection()) {
            case VERTICAL_UP:
                gr.translate(0, getSize().getHeight());
                gr.transform(AffineTransform.getQuadrantRotateInstance(-1));
                break;
            case VERTICAL_DOWN:
                gr.transform(AffineTransform.getQuadrantRotateInstance(1));
                gr.translate(0, -getSize().getWidth());
                break;
            default:
        }

        needsRotate = true;
        super.paintComponent(gr);
        needsRotate = false;
    }

    /**
     * Test method.
     *
     * @param args
     *          command line arguments.
     */
    public static void main(String[] args) {
        JFrame f = new JFrame("Test");
        f.setLayout(new FlowLayout());
        VerticalLabel rl = new VerticalLabel("BLAHBLAH");
        rl.setBackground(Color.ORANGE);
        rl.setOpaque(true);
        rl.setDirection(Direction.VERTICAL_DOWN);
        f.add(rl);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

}