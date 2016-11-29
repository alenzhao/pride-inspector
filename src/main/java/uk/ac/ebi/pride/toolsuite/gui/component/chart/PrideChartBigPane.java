package uk.ac.ebi.pride.toolsuite.gui.component.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.tabbedui.VerticalLayout;
import uk.ac.ebi.pride.toolsuite.chart.PrideChartType;
import uk.ac.ebi.pride.toolsuite.chart.dataset.PrideDataType;
import uk.ac.ebi.pride.toolsuite.chart.io.ElderJSONReader;
import uk.ac.ebi.pride.toolsuite.chart.io.PrideDataReader;
import uk.ac.ebi.pride.toolsuite.chart.io.QuartilesType;
import uk.ac.ebi.pride.toolsuite.chart.plot.*;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.prop.PropertyManager;
import uk.ac.ebi.pride.utilities.util.Tuple;

import javax.help.CSH;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Class to show a big size chart with a toolbar for browsing and manage the charts contained in PRIDE Inspector.</p>
 *
 * @author Antonio Fabregat
 * @author ypriverol
 *
 */
public class PrideChartBigPane extends PrideChartPane {

    private ChartTabPane container;
    private PrideChartType currentChartType;

    private JPanel optionPane;

    public PrideChartBigPane(ChartTabPane container, PrideDataReader reader, PrideChartType chartType) {
        super(reader);
        drawChart(chartType);

        this.container = container;
        this.currentChartType = chartType;
    }

    @Override
    protected String getTitle(PrideChartType chartType) {
        return chartType.getFullTitle();
    }

    @Override
    protected int getTitleFontSize() {
        return 20;
    }

    private JPanel getAvgOptionPane(final AverageMSPlot plot) {

        JPanel optionPane = new JPanel(new VerticalLayout());

        //Options title
        JLabel title = new JLabel("Chart Options");
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 15);
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        optionPane.add(titleBar);

        Map<PrideDataType, Boolean> optionList = plot.getOptionList();
        final ButtonGroup btnOptions = new ButtonGroup();
        JToggleButton jtb;
        for (PrideDataType dataType : optionList.keySet()) {
            jtb = new JRadioButton(dataType.getTitle());
            jtb.setEnabled(optionList.get(dataType));
            jtb.setSelected(dataType == PrideDataType.ALL_SPECTRA);
            jtb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Enumeration<AbstractButton> it = btnOptions.getElements();
                    AbstractButton option;
                    while (it.hasMoreElements()) {
                        option = it.nextElement();
                        if (option.isSelected()) {
                            plot.updateSpectraSeries(PrideDataType.findBy(option.getText()));
                        }
                    }
                }
            });
            jtb.setOpaque(false);
            optionPane.add(jtb);
            btnOptions.add(jtb);
        }

        return optionPane;
    }

    private JPanel getQuantitationOptionPane(final QuantitationChart plot) {

//        JPanel optionPane = new JPanel(new VerticalLayout());
//
//        //Options title
//        JLabel title = new JLabel("Chart Options");
//        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 15);
//        title.setFont(titleFont);
//        JPanel titleBar = new JPanel();
//        titleBar.add(title);
//        titleBar.setBackground(Color.WHITE);
//        optionPane.add(titleBar);
//
//        Map<String, Boolean> optionList = plot.getOptionStudyList();
//        final ButtonGroup btnOptions = new ButtonGroup();
//        JToggleButton jtb;
//        for (String dataType : optionList.keySet()) {
//            jtb = new JRadioButton(dataType);
//            jtb.setEnabled(optionList.get(dataType));
//            jtb.setSelected(dataType == PrideDataType.ALL_SPECTRA.getTitle());
//            jtb.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    Enumeration<AbstractButton> it = btnOptions.getElements();
//                    AbstractButton option;
//                    while (it.hasMoreElements()) {
//                        option = it.nextElement();
//                        if (option.isSelected()) {
//                            plot.updateSpectraSeries(option.getText());
//                        }
//                    }
//                }
//            });
//            jtb.setOpaque(false);
//            optionPane.add(jtb);
//            btnOptions.add(jtb);
//        }
//
//        return optionPane;

        JPanel optionPane = new JPanel(new VerticalLayout());

        //Options title
        JLabel title = new JLabel("Chart Options");
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 15);
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        optionPane.add(titleBar);

        Map<String, Tuple<Boolean, Boolean>> optionList = plot.getOptionStudyList();
        final java.util.List<JToggleButton> jtbList = new ArrayList<>();
        JToggleButton jtb;
        for (String dataType : optionList.keySet()) {
            jtb = new JCheckBox(dataType);
            jtb.setEnabled(optionList.get(dataType).getKey());
            jtb.setSelected(optionList.get(dataType).getValue());
            jtb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Iterator<JToggleButton> it = jtbList.iterator();
                    AbstractButton option;
                    while (it.hasNext()) {
                        option = it.next();
                        //plot.setVisible(option.isSelected(), PrideDataType.findBy(option.getText()));
                        plot.updateSpectraSeries(option.isSelected(), option.getText());
                    }
                }
            });
            jtb.setOpaque(false);
            optionPane.add(jtb);
            jtbList.add(jtb);
        }

        return optionPane;
    }

    private JPanel getPreMassesOptionPane(final PrecursorMassesPlot plot) {
        JPanel optionPane = new JPanel(new VerticalLayout());

        //Options title
        JLabel title = new JLabel("Chart Options");
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 15);
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        optionPane.add(titleBar);

        Map<PrideDataType, Boolean> optionList = plot.getOptionList();
        final ButtonGroup btnOptions = new ButtonGroup();
        JToggleButton jtb;
        for (PrideDataType dataType : optionList.keySet()) {
            jtb = new JRadioButton(dataType.getTitle());
            jtb.setEnabled(optionList.get(dataType));
            jtb.setSelected(dataType == PrideDataType.ALL_SPECTRA);
            jtb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Enumeration<AbstractButton> it = btnOptions.getElements();
                    AbstractButton option;
                    while (it.hasMoreElements()) {
                        option = it.nextElement();
                        if (option.isSelected()) {
                            plot.updateSpectraSeries(PrideDataType.findBy(option.getText()));
                        }
                    }
                }
            });
            jtb.setOpaque(false);
            optionPane.add(jtb);
            btnOptions.add(jtb);
        }

        return optionPane;
    }

    private JPanel getQuartilesOptionPane(final PrecursorMassesPlot plot) {
        JPanel optionPane = new JPanel(new VerticalLayout());

        optionPane.setBackground(Color.WHITE);

        //Options title
        JLabel title = new JLabel("Chart Quartiles");
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 15);
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        optionPane.add(titleBar);

        final ButtonGroup btnOptions = new ButtonGroup();
        JToggleButton jtb;
        for (QuartilesType quartilesType : QuartilesType.values()) {
            jtb = new JRadioButton(quartilesType.getReference());
            jtb.setSelected(quartilesType == QuartilesType.NONE);
            jtb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Enumeration<AbstractButton> it = btnOptions.getElements();
                    AbstractButton option;
                    while (it.hasMoreElements()) {
                        option = it.nextElement();
                        if (option.isSelected()) {
                            plot.updateQuartilesType(QuartilesType.getQuartilesType(option.getText()));
                        }
                    }
                }
            });
            jtb.setOpaque(false);
            optionPane.add(jtb);
            btnOptions.add(jtb);
        }

        return optionPane;
    }

    private JPanel getPeakIntensityOptionPane(final PeakIntensityPlot plot) {

        JPanel optionPane = new JPanel(new VerticalLayout());

        //Options title
        JLabel title = new JLabel("Chart Options");
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 15);
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        optionPane.add(titleBar);

        Map<PrideDataType, Boolean> optionList = plot.getOptionList();
        final java.util.List<JToggleButton> jtbList = new ArrayList<>();
        JToggleButton jtb;
        for (PrideDataType dataType : optionList.keySet()) {
            jtb = new JCheckBox(dataType.getTitle());
            jtb.setEnabled(optionList.get(dataType));
            jtb.setSelected(dataType == PrideDataType.ALL_SPECTRA);
            jtb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Iterator<JToggleButton> it = jtbList.iterator();
                    AbstractButton option;
                    while (it.hasNext()) {
                        option = it.next();
                        plot.setVisible(option.isSelected(), PrideDataType.findBy(option.getText()));
                    }
                }
            });
            jtb.setOpaque(false);
            optionPane.add(jtb);
            jtbList.add(jtb);
        }

        return optionPane;
    }

    private JPanel getPeakMSOptionPane(final PeaksMSPlot plot) {

        JPanel optionPane = new JPanel(new VerticalLayout());

        //Options title
        JLabel title = new JLabel("Chart Options");
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 15);
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        optionPane.add(titleBar);

        Map<PrideDataType, Boolean> optionList = plot.getOptionList();
        final java.util.List<JToggleButton> jtbList = new ArrayList<>();
        JToggleButton jtb;
        for (PrideDataType dataType : optionList.keySet()) {
            jtb = new JCheckBox(dataType.getTitle());
            jtb.setEnabled(optionList.get(dataType));
            jtb.setSelected(dataType == PrideDataType.ALL_SPECTRA);
            jtb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Iterator<JToggleButton> it = jtbList.iterator();
                    AbstractButton option;
                    while (it.hasNext()) {
                        option = it.next();
                        plot.setVisible(option.isSelected(), PrideDataType.findBy(option.getText()));
                    }
                }
            });
            jtb.setOpaque(false);
            optionPane.add(jtb);
            jtbList.add(jtb);
        }

        return optionPane;
    }

    private JPanel getDeltaMassOptionPane(final DeltaMZPlot plot) {

        JPanel optionPane = new JPanel(new VerticalLayout());

        //Options title
        JLabel title = new JLabel("Chart Options");
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 15);
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        optionPane.add(titleBar);

        Map<PrideDataType, Boolean> optionList = plot.getOptionList();
        final java.util.List<JToggleButton> jtbList = new ArrayList<>();
        JToggleButton jtb;
        for (PrideDataType dataType : optionList.keySet()) {
            jtb = new JCheckBox(dataType.getTitle());
            jtb.setEnabled(optionList.get(dataType));
            jtb.setSelected(dataType == PrideDataType.IDENTIFIED_SPECTRA);
            jtb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Iterator<JToggleButton> it = jtbList.iterator();
                    AbstractButton option;
                    while (it.hasNext()) {
                        option = it.next();
                        plot.setVisible(option.isSelected(), PrideDataType.findBy(option.getText()));
                    }
                }
            });
            jtb.setOpaque(false);
            optionPane.add(jtb);
            jtbList.add(jtb);
        }

        return optionPane;
    }

    private JPanel getMissCleavageOptionPane(final MissedCleavagesPlot plot) {

        JPanel optionPane = new JPanel(new VerticalLayout());

        //Options title
        JLabel title = new JLabel("Chart Options");
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 15);
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        optionPane.add(titleBar);

        Map<PrideDataType, Boolean> optionList = plot.getOptionList();
        final java.util.List<JToggleButton> jtbList = new ArrayList<>();
        JToggleButton jtb;
        for (PrideDataType dataType : optionList.keySet()) {
            jtb = new JCheckBox(dataType.getTitle());
            jtb.setEnabled(optionList.get(dataType));
            jtb.setSelected(dataType == PrideDataType.IDENTIFIED_SPECTRA);
            jtb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Iterator<JToggleButton> it = jtbList.iterator();
                    AbstractButton option;
                    while (it.hasNext()) {
                        option = it.next();
                        plot.setVisible(option.isSelected(), PrideDataType.findBy(option.getText()));
                    }
                }
            });
            jtb.setOpaque(false);
            optionPane.add(jtb);
            jtbList.add(jtb);
        }

        return optionPane;
    }

    @Override
    protected JPanel getMainPanel(JFreeChart chart, PrideChartType chartType) {
        JPanel mainPanel = new JPanel(new BorderLayout());

        switch (chartType) {
            case AVERAGE_MS:
                optionPane = getAvgOptionPane((AverageMSPlot) chart.getPlot());
                break;
            case PRECURSOR_MASSES:
                optionPane = getPreMassesOptionPane((PrecursorMassesPlot) chart.getPlot());
                JPanel quartilesPane = getQuartilesOptionPane((PrecursorMassesPlot) chart.getPlot());
                optionPane.add(quartilesPane);
                break;
            case PEAK_INTENSITY:
                optionPane = getPeakIntensityOptionPane((PeakIntensityPlot) chart.getPlot());
                break;
            case DELTA_MASS:
                optionPane = getDeltaMassOptionPane((DeltaMZPlot) chart.getPlot());
                break;
            case MISSED_CLEAVAGES:
                optionPane = getMissCleavageOptionPane((MissedCleavagesPlot) chart.getPlot());
                break;
            case PEAKS_MS:
                optionPane = getPeakMSOptionPane((PeaksMSPlot) chart.getPlot());
                break;
            case QUANTITATION_PEPTIDES:
                optionPane = getQuantitationOptionPane((QuantitationChart) chart.getPlot());
                break;
            default:
                optionPane = null;
                break;
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        if (optionPane != null) {
            optionPane.setBackground(Color.WHITE);
            optionPane.setMinimumSize(new Dimension(100, 200));
            optionPane.setVisible(false);
            mainPanel.add(optionPane, BorderLayout.EAST);
        }

        return mainPanel;
    }

    @Override
    protected JPanel getToolsPanel(PrideChartType chartType) {
        JPanel toolsPanel = new JPanel();

        // get property manager
        PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();

        // Previous
        Icon previousIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_previous.icon.large"));
        String previousTooltip = propMgr.getProperty("chart_previous.tooltip");
        PrideChartButton btnPrevious = new PrideChartButton(previousIcon, previousTooltip);

        btnPrevious.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                PrideChartType previousChartType = currentChartType.previous();
                drawChart(previousChartType);
                currentChartType = previousChartType;
            }
        });
        toolsPanel.add(btnPrevious);

        // Show all charts
        Icon allChartIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_all_charts.icon.large"));
        String allChartTooltip = propMgr.getProperty("chart_all_charts.tooltip");
        PrideChartButton btnAllChart = new PrideChartButton(allChartIcon, allChartTooltip);

        btnAllChart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                container.showThumbnailView(reader);
            }
        });
        toolsPanel.add(btnAllChart);


        // Next
        Icon nextIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_next.icon.large"));
        String nextTooltip = propMgr.getProperty("chart_next.tooltip");
        PrideChartButton btnNext = new PrideChartButton(nextIcon, nextTooltip);

        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                PrideChartType nextChartType = currentChartType.next();
                drawChart(nextChartType);
                currentChartType = nextChartType;
            }
        });
        toolsPanel.add(btnNext);


        // Info
        Icon infoIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_info.icon.large"));
        String infoTooltip = propMgr.getProperty("chart_info.tooltip");
        PrideChartButton btnInfo = new PrideChartButton(infoIcon, infoTooltip);
        int chartID = ElderJSONReader.getChartID(chartType);
        CSH.setHelpIDString(btnInfo, "help.chart." + chartID);
        btnInfo.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        toolsPanel.add(btnInfo);


        // Options
        Icon optionIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_options.icon.large"));
        String optionTooltip = propMgr.getProperty("chart_options.tooltip");
        final PrideChartButton btnOptions = new PrideChartButton(optionIcon, optionTooltip);

        btnOptions.setEnabled(chartType == PrideChartType.AVERAGE_MS ||
                chartType == PrideChartType.PRECURSOR_MASSES ||
                chartType == PrideChartType.PEAK_INTENSITY ||
                chartType == PrideChartType.DELTA_MASS ||
                chartType == PrideChartType.PEAKS_MS ||
                chartType == PrideChartType.MISSED_CLEAVAGES ||
                chartType == PrideChartType.QUANTITATION_PEPTIDES
        );

        // If the chart has options, the behaviour of the options button has to be like a standard JToggleButton
        if (btnOptions.isEnabled()) {
            btnOptions.setSelected(true);
            btnOptions.setKeepSelected(true);
            //Only needed an action listener if the chart has legend
            btnOptions.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    optionPane.setVisible(! optionPane.isVisible());
                }
            });
        }
        toolsPanel.add(btnOptions);

        toolsPanel.setBackground(Color.WHITE);

        return toolsPanel;
    }

    @Override
    protected JPanel getErrorPanel(String[] errMsgList) {
        JPanel errorPanel = new JPanel();

        errorPanel.setBackground(Color.WHITE);
        errorPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.WEST;

        JPanel center = new JPanel(new VerticalLayout());
        center.setBackground(Color.WHITE);

        // get property manager
        DesktopContext context = uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();

        Icon warnIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_warning.icon.medium"));
        int fontSize = 15;
        JLabel iconLabel = new JLabel(warnIcon, JLabel.CENTER);
        iconLabel.setAlignmentX(0.5f);
        center.add(iconLabel);

        String msg = "This chart could not be generated because:";
        JLabel common = new JLabel(msg);
        common.setFont(new Font("Serif", Font.BOLD, fontSize));
        common.setAlignmentX(0.5f);
        center.add(common);

        for(String message : errMsgList){
            JLabel error = new JLabel(" - "+message);
            error.setFont(new Font("Serif", Font.PLAIN, fontSize));
            error.setAlignmentX(0.5f);
            center.add(error);
        }

        errorPanel.add(center, c);

        return errorPanel;
    }
}
