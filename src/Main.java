import javax.swing.*;
import java.awt.*;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;


public class Main {

    private JLabel LabelMain;
    private JTextField pasteCodeHereTextField;
    private JButton RunCode;
    private JLabel result;
    private JPanel panel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Anti Ryd Device");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JPanel panel = new JPanel();
        frame.add(panel);

        JLabel LabelMain = new JLabel("Anti Ryd Device");
        panel.add(LabelMain);
        
        JTextArea pasteCodeHereTextField = new JTextArea(
                    "public class DynamicCode {\n public static void main(String[] args) {\n //Replace Me!\n }\n }"
        );

        pasteCodeHereTextField.setFont(new Font("Arial", Font.PLAIN, 15));
        pasteCodeHereTextField.setLineWrap(true);
        pasteCodeHereTextField.setWrapStyleWord(true);
        pasteCodeHereTextField.setSize(200, 200);
        panel.add(pasteCodeHereTextField);

        JButton runCode = new JButton("Run Code");
        panel.add(runCode);

        JLabel result = new JLabel();
        panel.add(result);

        //move all the components to the middle of the screen
        panel.setLayout(null);
        LabelMain.setBounds(200, 50, 100, 25);
        pasteCodeHereTextField.setBounds(100, 100, 300, 200);
        runCode.setBounds(200, 325, 100, 25);
        result.setBounds(200, 400, 300, 25);

        runCode.addActionListener(e -> {

            String code = pasteCodeHereTextField.getText();
            try {

                // Write the code to a file
                File file = new File("DynamicCode.java");
                FileWriter writer = new FileWriter(file, true);
                writer.write(code);
                writer.close();
                //if the file is not empty, delete the contents
                if (file.length() != 0) {
                    file.delete();
                    file.createNewFile();
                    writer = new FileWriter(file);
                    writer.write(code);
                    writer.close();
                }

                // Compile the code
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                int result1 = compiler.run(null, null, null, file.getPath());
                if (result1 == 0) {
                    System.out.println("Compilation is successful");
                    compileAndRun("DynamicCode");

                    String output = compileAndRun("DynamicCode");
                    result.setText("The Code Output is: " + output);
//                    result.setText("output of the code: " + );

                    // Load the compiled class
                    Class<?> cls = Class.forName("DynamicCode");
                    Method mainMethod = cls.getMethod("main", String[].class);

                    // Invoke the main method
                    String[] mainArgs = new String[]{};
                    mainMethod.invoke(null, (Object) mainArgs);
                } else {
                    System.out.println("Compilation Failed");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static String compileAndRun(String className) {
        // Compile the class
        ProcessBuilder pb = new ProcessBuilder("javac", className + ".java");
        try {
            Process p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error compiling class " + className);
            e.printStackTrace();
        }

        // Run the class
        pb = new ProcessBuilder("java", className);
        try {
            Process p = pb.start();

            // Capture the output
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (InputStream is = p.getInputStream()) {
                int b;
                while ((b = is.read()) != -1) {
                    baos.write(b);
                }
            }
            String output = baos.toString();

            // Wait for the process to finish
            p.waitFor();

            // Print the output
            System.out.println(output);

            JLabel result = new JLabel();
            result.setText(output);
            return output;

        } catch (IOException | InterruptedException e) {
            System.out.println("Error running class " + className);
            e.printStackTrace();
        }
        return "";
    }
}
