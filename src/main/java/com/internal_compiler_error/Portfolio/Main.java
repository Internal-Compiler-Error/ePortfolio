package com.internal_compiler_error.Portfolio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

class GreetingPane extends JPanel {
    GreetingPane() {

        var str = String.format("Welcome to ePortfolio. %n%n%n%n" +
                "Choose a command from the “Commands” menu to buy or sell  an investment, update prices for all " +
                "investments, get gain for the portfolio, search for relevant investments, or quit the program.");

        var textArea = new JTextArea(str);
        textArea.setEditable(false);
        setLayout(new BorderLayout());

        textArea.setLineWrap(true);
        add(textArea, BorderLayout.CENTER);
    }
}

class MessagePane extends JPanel {
    private JLabel label = new JLabel();
    private JTextArea textArea = new JTextArea("");

    MessagePane() {
        label.setText("Messages");

        setLayout(new BorderLayout());
        textArea.setEditable(false);

        var scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        label.setLabelFor(scrollPane);


        var container = new JPanel();
        var containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);

        container.add(label);
        container.add(scrollPane);

        add(container, BorderLayout.CENTER);
    }

    MessagePane(String description) {
        this();

        label.setText(description);
    }

    void setText(String s) {
        textArea.setText(s);
    }
}

class InputPanel extends JPanel {
    private JLabel label = new JLabel("");
    private final JTextField input = new JTextField(60);

    InputPanel(String inputName) {
        var layout = new BoxLayout(this, BoxLayout.X_AXIS);

        setLayout(layout);
        label.setText(inputName);
        label.setLabelFor(input);


        add(label);
        add(input);
    }

    InputPanel(String inputName, boolean editable) {
        var layout = new BoxLayout(this, BoxLayout.X_AXIS);

        setLayout(layout);
        label.setText(inputName);
        label.setLabelFor(input);

        input.setEditable(editable);

        add(label);
        add(input);
    }

    String getText() {
        return input.getText();
    }

    void setText(String s) {
        input.setText(s);
    }
}

class BuyComponent extends JPanel {
    private JLabel label = new JLabel("Buying an investment");
    private String[] investmentsTypes = {"Stock", "Mutual Fund"};
    private JComboBox<String> investmentTypeInput = new JComboBox<>(investmentsTypes);
    private InputPanel symbolInput = new InputPanel("Symbol");
    private InputPanel nameInput = new InputPanel("Name");
    private InputPanel quantityInput = new InputPanel("Quantity");
    private InputPanel priceInput = new InputPanel("Price");


    BuyComponent() {
        label.setLabelFor(investmentTypeInput);
        label.setLabelFor(symbolInput);
        label.setLabelFor(nameInput);
        label.setLabelFor(quantityInput);
        label.setLabelFor(priceInput);

        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        add(label);
        add(investmentTypeInput);
        add(symbolInput);
        add(nameInput);
        add(quantityInput);
        add(priceInput);
    }


    public JComboBox<String> getInvestmentTypeInput() {
        return investmentTypeInput;
    }

    public InputPanel getSymbolInput() {
        return symbolInput;
    }

    public InputPanel getNameInput() {
        return nameInput;
    }

    public InputPanel getQuantityInput() {
        return quantityInput;
    }

    public InputPanel getPriceInput() {
        return priceInput;
    }

    List<InputPanel> getInputPanels() {
        return List.of(symbolInput, nameInput, quantityInput, priceInput);
    }
}

class BuyButtons extends JPanel {
    private JButton resetButton = new JButton("Reset");
    private JButton buyButton = new JButton("Buy");

    BuyButtons(ActionListener actionListener) {
        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        resetButton.addActionListener(actionListener);
        buyButton.addActionListener(actionListener);

        add(resetButton);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(buyButton);
    }
}

class BuyPane extends JPanel {
    private BuyComponent buyComponent = new BuyComponent();
    private BuyButtons buyButtons;
    private MessagePane messagePane = new MessagePane();
    private ePortfolio portfolio;


    BuyPane(ePortfolio portfolio) {
        this.portfolio = portfolio;


        buyButtons = new BuyButtons(actionEvent -> {
            var button = (JButton) actionEvent.getSource();

            if (button.getText().equals("Buy")) {
                var name = buyComponent.getNameInput().getText();
                var symbol = buyComponent.getSymbolInput().getText();
                var quantity = buyComponent.getQuantityInput().getText();
                var price = buyComponent.getQuantityInput().getText();
                var investmentType = (String) buyComponent.getInvestmentTypeInput().getSelectedItem();

                try {
                    var type = investmentType.equalsIgnoreCase("Mutual Fund")
                            ? InvestmentType.MUTUAL_FUND : InvestmentType.STOCK;
                    var priceBigDecimal = new BigDecimal(price);

                    if (symbol.isEmpty() || name.isEmpty() || Integer.parseInt(quantity) <= 0 || Double.parseDouble(price) <= 0) {
                        throw new Exception("Invalid inputs");
                    }

                    portfolio.buy(type,
                            symbol,
                            name,
                            priceBigDecimal,
                            Integer.parseInt(quantity));
                    JOptionPane.showMessageDialog(this, "Success!");
                    messagePane.setText("Success");
                } catch (Exception e) {
                    var stringWriter = new StringWriter();
                    var printWriter = new PrintWriter(stringWriter);

                    e.printStackTrace(printWriter);
                    messagePane.setText(stringWriter.toString());
                    JOptionPane.showMessageDialog(this, String.format("Error while trying to buy product. %n Reason: %s", e.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                }


            } else if (button.getText().equals("Reset")) {
                buyComponent.getInputPanels().forEach(inputPanel -> inputPanel.setText(""));
            } else {
                System.out.println("WTF");
            }
        });

        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);

        var pane = new JPanel();
        var paneLayout = new BoxLayout(pane, BoxLayout.LINE_AXIS);
        pane.setLayout(paneLayout);

        pane.add(buyComponent);
        pane.add(buyButtons);


        setLayout(layout);
        add(pane);
        add(messagePane);
    }
}

class SellComponent extends JPanel {
    private JLabel label = new JLabel("Selling an investment");
    private InputPanel symbolInput = new InputPanel("Symbol");
    private InputPanel quantityInput = new InputPanel("Quantity");
    private InputPanel priceInput = new InputPanel("Price");


    SellComponent() {
        label.setLabelFor(symbolInput);
        label.setLabelFor(quantityInput);
        label.setLabelFor(priceInput);

        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        add(label);
        add(symbolInput);
        add(quantityInput);
        add(priceInput);
    }

    public InputPanel getSymbolInput() {
        return symbolInput;
    }

    public InputPanel getQuantityInput() {
        return quantityInput;
    }

    public InputPanel getPriceInput() {
        return priceInput;
    }

    List<InputPanel> getInputPanels() {
        return List.of(symbolInput, quantityInput, priceInput);
    }
}

class SellButtons extends JPanel {
    private JButton resetButton = new JButton("Reset");
    private JButton buyButton = new JButton("Sell");

    SellButtons(ActionListener actionListener) {
        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        resetButton.addActionListener(actionListener);
        buyButton.addActionListener(actionListener);

        add(resetButton);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(buyButton);
    }
}

class SellPane extends JPanel {
    private SellComponent sellComponent = new SellComponent();
    private SellButtons sellButtons;
    private MessagePane messagePane = new MessagePane();
    private ePortfolio portfolio;

    SellPane(ePortfolio portfolio) {
        this.portfolio = portfolio;


        sellButtons = new SellButtons(actionEvent -> {
            var button = (JButton) actionEvent.getSource();

            if (button.getText().equals("Sell")) {
                try {
                    var symbol = sellComponent.getSymbolInput().getText();
                    var price = sellComponent.getPriceInput().getText();
                    var quantity = sellComponent.getQuantityInput().getText();

                    if (symbol.isEmpty() || Integer.parseInt(quantity) <= 0 || Double.parseDouble(price) <= 0) {
                        throw new Exception("Invalid inputs");
                    }

                    var profit = portfolio.sell(symbol, Integer.parseInt(quantity), new BigDecimal(price));

                    messagePane.setText(String.format("Success profit: %s", profit.toString()));
                    JOptionPane.showMessageDialog(this, "Success!");
                } catch (Exception e) {
                    var stringWriter = new StringWriter();
                    var printWriter = new PrintWriter(stringWriter);

                    e.printStackTrace(printWriter);
                    messagePane.setText(stringWriter.toString());
                    JOptionPane.showMessageDialog(this, String.format("Error while trying to sell product. %n Reason: %s", e.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (button.getText().equals("Reset")) {
                sellComponent.getInputPanels().forEach(inputPanel -> inputPanel.setText(""));
            }
        });

        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);

        var pane = new JPanel();
        var paneLayout = new BoxLayout(pane, BoxLayout.LINE_AXIS);
        pane.setLayout(paneLayout);

        pane.add(sellComponent);
        pane.add(sellButtons);


        setLayout(layout);
        add(pane);
        add(messagePane);
    }
}

class UpdateComponent extends JPanel {
    private JLabel label = new JLabel("Updating investments");
    private InputPanel symbolInput = new InputPanel("Symbol", false);
    private InputPanel nameInput = new InputPanel("Name", false);
    private InputPanel priceInput = new InputPanel("Price");


    UpdateComponent() {
        label.setLabelFor(symbolInput);
        label.setLabelFor(nameInput);
        label.setLabelFor(priceInput);

        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        add(label);
        add(symbolInput);
        add(nameInput);
        add(priceInput);
    }

    public InputPanel getSymbolInput() {
        return symbolInput;
    }

    public InputPanel getNameInput() {
        return nameInput;
    }

    public InputPanel getPriceInput() {
        return priceInput;
    }

    List<InputPanel> getInputPanels() {
        return List.of(symbolInput, nameInput, priceInput);
    }
}

class UpdateButtons extends JPanel {
    private JButton previousButton = new JButton("Previous");
    private JButton nextButton = new JButton("Next");
    private JButton saveButton = new JButton("Save");

    UpdateButtons(ActionListener actionListener) {
        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        previousButton.addActionListener(actionListener);
        nextButton.addActionListener(actionListener);
        saveButton.addActionListener(actionListener);

        add(previousButton);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(nextButton);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(saveButton);
    }
}

class UpdatePane extends JPanel {
    private UpdateComponent updateComponent = new UpdateComponent();
    private UpdateButtons updateButtons;
    private MessagePane messagePane = new MessagePane();
    private ePortfolio portfolio;
    private int index = 0;


    UpdatePane(ePortfolio portfolio) {
        this.portfolio = portfolio;

        updateGUIWithPortfolio(index);


        updateButtons = new UpdateButtons(actionEvent -> {
            var button = (JButton) actionEvent.getSource();

            if (button.getText().equals("Previous")) {
                index = (index - 1 + portfolio.getSize()) % portfolio.getSize();
                updateGUIWithPortfolio(index);
            } else if (button.getText().equals("Next")) {
                index = (index + 1) % portfolio.getSize();
                updateGUIWithPortfolio(index);
            } else if (button.getText().equals("Save")) {
                try {
                    var priceStr = updateComponent.getPriceInput().getText();

                    if (Double.parseDouble(priceStr) <= 0) {
                        throw new Exception("Invalid Input");
                    }

                    portfolio.get(index).updatePrice(new BigDecimal(priceStr));

                    messagePane.setText("Success");
                    JOptionPane.showMessageDialog(this, "Success!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, String.format("Error while trying to update product. %n Reason: %s", e.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);

        var pane = new JPanel();
        var paneLayout = new BoxLayout(pane, BoxLayout.LINE_AXIS);
        pane.setLayout(paneLayout);

        pane.add(updateComponent);
        pane.add(updateButtons);


        setLayout(layout);
        add(pane);
        add(messagePane);
    }

    private void updateGUIWithPortfolio(int index) {
        updateComponent.getSymbolInput().setText(portfolio.get(index).getSymbol());
        updateComponent.getNameInput().setText(portfolio.get(index).getName());
        updateComponent.getPriceInput().setText(portfolio.get(index).getPrice().toString());
    }
}

class GainPane extends JPanel {
    private InputPanel totalGainPanel = new InputPanel("Total Gain", false);
    private MessagePane messagePane = new MessagePane("Individual Gains");

    GainPane(String totalGain, String individualGains) {
        var upperPanel = new JPanel();
        var upperPanelLayout = new BoxLayout(upperPanel, BoxLayout.Y_AXIS);

        var label = new JLabel("Getting Total Gain");

        upperPanel.setLayout(upperPanelLayout);
        upperPanel.add(label);
        upperPanel.add(totalGainPanel);

        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);


        totalGainPanel.setText(totalGain);
        messagePane.setText(individualGains);

        add(upperPanel);
        add(messagePane);
    }
}


class QueryComponent extends JPanel {
    private JLabel label = new JLabel("Searching investments");
    private InputPanel symbolInput = new InputPanel("Symbol");
    private InputPanel nameInput = new InputPanel("Name Keywords");
    private InputPanel lowPriceInput = new InputPanel("Low Price");
    private InputPanel highPriceInput = new InputPanel("High Price");


    QueryComponent() {
        label.setLabelFor(symbolInput);
        label.setLabelFor(nameInput);
        label.setLabelFor(lowPriceInput);
        label.setLabelFor(highPriceInput);

        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        add(label);

        add(symbolInput);
        add(nameInput);
        add(lowPriceInput);
        add(highPriceInput);
    }


    public InputPanel getSymbolInput() {
        return symbolInput;
    }

    public InputPanel getNameInput() {
        return nameInput;
    }

    public InputPanel getLowPriceInput() {
        return lowPriceInput;
    }

    public InputPanel getHighPriceInput() {
        return highPriceInput;
    }

    List<InputPanel> getInputPanels() {
        return List.of(symbolInput, nameInput, lowPriceInput, highPriceInput);
    }
}

class QueryButtons extends JPanel {
    private JButton resetButton = new JButton("Reset");
    private JButton searchButton = new JButton("Search");

    QueryButtons(ActionListener actionListener) {
        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        resetButton.addActionListener(actionListener);
        searchButton.addActionListener(actionListener);

        add(resetButton);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(searchButton);
    }
}

class QueryPane extends JPanel {
    private QueryComponent queryComponent = new QueryComponent();
    private QueryButtons queryButtons;
    private MessagePane messagePane = new MessagePane("Search Results");
    private ePortfolio portfolio;

    QueryPane(ePortfolio portfolio) {
        this.portfolio = portfolio;

        var pane = new JPanel();
        var paneLayout = new BoxLayout(pane, BoxLayout.LINE_AXIS);
        pane.setLayout(paneLayout);

        queryButtons = new QueryButtons(actionEvent -> {
            var button = (JButton) actionEvent.getSource();


            if (button.getText().equals("Search")) {
                var symbol = queryComponent.getSymbolInput().getText();
                var name = queryComponent.getNameInput().getText();
                var price = String.format("%s-%s",
                        queryComponent.getLowPriceInput().getText(),
                        queryComponent.getHighPriceInput().getText());

                var results = portfolio.query(symbol, name, price);

                messagePane.setText(String.join("%n", results));
            } else if (button.getText().equals("Reset")) {
                queryComponent.getInputPanels().forEach(inputPanel -> inputPanel.setText(""));
            }
        });


        pane.add(queryComponent);
        pane.add(queryButtons);


        var layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);
        add(pane);
        add(messagePane);
    }
}

public class Main extends JFrame {
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu = new JMenu("Commands");

    private JMenuItem buyMenuItem = new JMenuItem("Buy");
    private JMenuItem sellMenuItem = new JMenuItem("Sell");
    private JMenuItem updateMenuItem = new JMenuItem("Update");
    private JMenuItem getGainMenuItem = new JMenuItem("Calculate Gain");
    private JMenuItem searchMenuItem = new JMenuItem("Search");
    private JMenuItem quitMenuItem = new JMenuItem("Quit");

    private ePortfolio portfolio = new ePortfolio();

    private JPanel currentPane = new GreetingPane();


    public Main() {
        setTitle("ePortfolio");

        menu.add(buyMenuItem);
        menu.add(sellMenuItem);
        menu.add(updateMenuItem);
        menu.add(getGainMenuItem);
        menu.add(searchMenuItem);
        menu.add(quitMenuItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        var layout = new BorderLayout();
        setLayout(layout);


        buyMenuItem.addActionListener(actionEvent -> {
            remove(currentPane);
            currentPane = new BuyPane(portfolio);
            add(currentPane);
            validate();
            repaint();
        });

        sellMenuItem.addActionListener(actionEvent -> {
            remove(currentPane);
            currentPane = new SellPane(portfolio);
            add(currentPane);
            validate();
            repaint();
        });


        getGainMenuItem.addActionListener(actionEvent -> {
            var individualGains = portfolio.getInvestments()
                    .stream()
                    .map(investment -> String.format("%s : %s", investment.getName(), investment.getGain().toString()))
                    .collect(Collectors.toList());


            remove(currentPane);
            currentPane = new GainPane(portfolio.calculateGain(), String.join(System.lineSeparator(), individualGains));
            add(currentPane);
            validate();
            repaint();
        });

        updateMenuItem.addActionListener(actionEvent -> {
            if (!portfolio.isEmpty()) {
                remove(currentPane);
                currentPane = new UpdatePane(portfolio);
                add(currentPane);
                validate();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "No investments in the portfolio, no point trying to update anything");
            }

        });
        searchMenuItem.addActionListener(actionEvent -> {
            remove(currentPane);
            currentPane = new QueryPane(portfolio);
            add(currentPane);
            validate();
            repaint();
        });

        quitMenuItem.addActionListener(actionEvent -> {
            dispose();
        });

        add(currentPane, BorderLayout.CENTER);
    }


    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        var app = new Main();

        app.setSize(new Dimension(500, 600));

        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setVisible(true);
    }
}
