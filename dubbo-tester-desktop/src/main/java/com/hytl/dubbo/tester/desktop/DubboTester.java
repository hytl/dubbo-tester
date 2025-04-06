package com.hytl.dubbo.tester.desktop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hytl.dubbo.tester.core.DubboClient;
import com.hytl.dubbo.tester.core.config.DubboClientConfig;
import com.hytl.dubbo.tester.core.config.RegistryConfiguration;
import com.hytl.dubbo.tester.core.config.ServiceConfiguration;
import com.hytl.dubbo.tester.core.config.enums.RegistryType;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ExecutionException;

public class DubboTester extends JFrame {
    // UI Components
    private JComboBox<String> registryType;
    private JTextField registryAddress;
    private JTextField serviceInterface;
    private JTextField methodName;
    private JTextField parameterTypes;
    private JTextArea parameters;
    private JTextArea resultArea;
    private JButton testButton;

    public DubboTester() {
        initUI();
    }

    private void initUI() {
        setTitle("Dubbo Test Client");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 800);
        Image icon = Toolkit.getDefaultToolkit().getImage(
                DubboTester.class.getResource("/assets/logo/favicon.png")
        );
        setIconImage(icon);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Registry Configuration Panel
        JPanel registryPanel = createRegistryPanel();
        mainPanel.add(registryPanel, BorderLayout.NORTH);

        // Service Configuration Panel
        JPanel servicePanel = createServicePanel();
        mainPanel.add(servicePanel, BorderLayout.CENTER);

        // Result Panel
        JPanel resultPanel = createResultPanel();
        mainPanel.add(resultPanel, BorderLayout.SOUTH);

        add(mainPanel);
        centerWindow();
    }

    private JPanel createRegistryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Registry Configuration"));

        registryType = new JComboBox<>(new String[]{"Nacos", "Zookeeper"});
        // registryAddress = new JTextField("zookeeper://127.0.0.1:2181");
        registryAddress = new JTextField("nacos://127.0.0.1:8848");

        panel.add(new JLabel("Registry Type:"));
        panel.add(registryType);
        panel.add(new JLabel("Registry Address:"));
        panel.add(registryAddress);

        return panel;
    }

    private JPanel createServicePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Service Configuration"));

        serviceInterface = new JTextField("org.apache.dubbo.samples.quickstart.dubbo.api.DemoService");
        methodName = new JTextField("sayHello");
        parameterTypes = new JTextField("java.lang.String");
        parameters = new JTextArea("[\"world\"]");
        parameters.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(parameters);

        panel.add(new JLabel("Service Interface:"));
        panel.add(serviceInterface);
        panel.add(new JLabel("Method Name:"));
        panel.add(methodName);
        panel.add(new JLabel("Parameter Types (comma separated):"));
        panel.add(parameterTypes);
        panel.add(new JLabel("Parameters (JSON array):"));
        panel.add(scrollPane);

        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Result Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(950, 400));

        // Control Panel
        JPanel controlPanel = new JPanel();
        testButton = new JButton("Invoke Service");
        testButton.addActionListener(e -> invokeService());
        testButton.setFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.add(testButton);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void invokeService() {
        resultArea.setForeground(Color.BLUE);
        resultArea.setText("Invoking service...");

        new SwingWorker<Object, Void>() {
            private Exception exception;

            @Override
            protected Object doInBackground() throws Exception {
                try {
                    // 1. Create Registry Configuration
                    RegistryConfiguration registryConfiguration = new RegistryConfiguration(
                            RegistryType.valueOf(((String) registryType.getSelectedItem()).toUpperCase())
                            , registryAddress.getText().trim());

                    // 2. Build Service Configuration
                    ServiceConfiguration serviceConfiguration = new ServiceConfiguration(
                            serviceInterface.getText().trim(), methodName.getText().trim()
                            , parameterTypes.getText().trim().split("\\s*,\\s*")
                            , new ObjectMapper().readValue(parameters.getText().trim(), Object[].class));

                    // 3. Build DubboClientConfig
                    DubboClientConfig dubboClientConfig = new DubboClientConfig(registryConfiguration, serviceConfiguration);

                    return DubboClient.execute(dubboClientConfig);
                } catch (Exception e) {
                    exception = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    if (exception != null) throw exception;

                    Object result = get();
                    showSuccessResult(result);
                } catch (InterruptedException | ExecutionException e) {
                    showErrorResult(e.getCause());
                } catch (Exception e) {
                    showErrorResult(e);
                }
            }
        }.execute();
    }

    private void showSuccessResult(Object result) {
        resultArea.setForeground(new Color(0, 128, 0));
        try {
            String formatted = new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result);
            resultArea.setText("Invoke Success:\n" + formatted);
        } catch (JsonProcessingException e) {
            resultArea.setText("Invoke Success (Raw):\n" + result);
        }
    }

    private void showErrorResult(Throwable ex) {
        resultArea.setForeground(Color.RED);
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        resultArea.setText("Invoke Failed:\n" + sw);
    }

    private void centerWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2,
                (screenSize.height - getHeight()) / 2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DubboTester client = new DubboTester();
            client.setVisible(true);
        });
    }
}