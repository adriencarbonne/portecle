/*
 * DViewPEM.java
 * This file is part of Portecle, a multipurpose keystore and certificate tool.
 *
 * Copyright © 2004 Wayne Grant, waynedgrant@hotmail.com
 *             2006 Ville Skyttä, ville.skytta@iki.fi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.sf.portecle.gui.crypto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.sf.portecle.crypto.CryptoException;

import org.bouncycastle.openssl.PEMWriter;

/**
 * Displays an X.509 object's PEM encoding.
 */
public class DViewPEM
    extends JDialog
{
    /** Resource bundle */
    private static ResourceBundle m_res = ResourceBundle.getBundle("net/sf/portecle/gui/crypto/resources");

    /** Panel to hold OK button */
    private JPanel m_jpOK;

    /** OK button to dismiss dialog */
    private JButton m_jbOK;

    /** Panel to hold scroll pane in */
    private JPanel m_jpPEM;

    /** Scroll pane to hold text area in */
    private JScrollPane m_jspPEM;

    /** Text area to display PEM encoding in */
    private JTextArea m_jtaPEM;

    /** Stores object to display */
    private Object m_object;

    /**
     * Creates new DViewPEM dialog where the parent is a frame.
     *
     * @param parent Parent frame
     * @param sTitle The dialog title
     * @param bModal Is dialog modal?
     * @param obj Object to display encoding for
     * @throws CryptoException A problem was encountered getting the
     * object's PEM encoding
     */
    public DViewPEM(JFrame parent, String sTitle, boolean bModal, Object obj)
        throws CryptoException
    {
        super(parent, sTitle, bModal);
        m_object = obj;
        initComponents();
    }

    /**
     * Creates new DViewPEM dialog where the parent is a dialog.
     *
     * @param parent Parent dialog
     * @param sTitle The dialog title
     * @param bModal Is dialog modal?
     * @param obj Object to display encoding for
     * @throws CryptoException A problem was encountered getting the
     * object's PEM encoding
     */
    public DViewPEM(JDialog parent, String sTitle, boolean bModal, Object obj)
        throws CryptoException
    {
        super(parent, sTitle, bModal);
        m_object = obj;
        initComponents();
    }

    /**
     * Initialise the dialog's GUI components.
     *
     * @throws CryptoException A problem was encountered getting the
     * object's PEM encoding
     */
    private void initComponents()
        throws CryptoException
    {
        StringWriter encoded = new StringWriter();
        PEMWriter pw = new PEMWriter(encoded);
        try {
            pw.writeObject(m_object);
        }
        catch (IOException e) {
            throw new CryptoException(
                m_res.getString("DViewPEM.exception.message"), e);
        }
        finally {
            try {
                pw.close();
            }
            catch (IOException e) { /* Ignore */
            }
        }

        m_jpOK = new JPanel(new FlowLayout(FlowLayout.CENTER));

        m_jbOK = new JButton(m_res.getString("DViewPEM.m_jbOK.text"));
        m_jbOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                okPressed();
            }
        });

        m_jpOK.add(m_jbOK);

        m_jpPEM = new JPanel(new BorderLayout());
        m_jpPEM.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Load text area with the PEM encoding
        m_jtaPEM = new JTextArea(encoded.toString());
        m_jtaPEM.setCaretPosition(0);
        m_jtaPEM.setEditable(false);
        m_jtaPEM.setFont(new Font("Monospaced", Font.PLAIN,
            m_jtaPEM.getFont().getSize()));

        m_jspPEM = new JScrollPane(m_jtaPEM,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        m_jspPEM.setPreferredSize(new Dimension(500, 300));
        m_jpPEM.add(m_jspPEM, BorderLayout.CENTER);

        getContentPane().add(m_jpPEM, BorderLayout.CENTER);
        getContentPane().add(m_jpOK, BorderLayout.SOUTH);

        setResizable(true);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent evt)
            {
                closeDialog();
            }
        });

        getRootPane().setDefaultButton(m_jbOK);

        pack();

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                m_jbOK.requestFocus();
            }
        });
    }

    /**
     * OK button pressed or otherwise activated.
     */
    private void okPressed()
    {
        closeDialog();
    }

    /**
     * Hides the dialog.
     */
    private void closeDialog()
    {
        setVisible(false);
        dispose();
    }
}