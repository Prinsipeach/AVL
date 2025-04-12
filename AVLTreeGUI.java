import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class AVLNode {
    int key;
    int height;
    AVLNode left;
    AVLNode right;

    AVLNode(int key) {
        this.key = key;
        this.height = 1;
    }
}

class AVLTree {
    private AVLNode root;

    // Get height of a node
    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    // Get balance factor of a node
    private int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    // Right rotate subtree rooted with y
    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    // Left rotate subtree rooted with x
    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    // Insert a key
    public void insert(int key) {
        root = insert(root, key);
    }

    private AVLNode insert(AVLNode node, int key) {
        if (node == null) {
            return new AVLNode(key);
        }

        if (key < node.key) {
            node.left = insert(node.left, key);
        } else if (key > node.key) {
            return node; // Duplicate keys not allowed
        } else {
            node.right = insert(node.right, key);
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && key < node.left.key) {
            return rightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && key > node.right.key) {
            return leftRotate(node);
        }

        // Left Right Case
        if (balance > 1 && key > node.left.key) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && key < node.right.key) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    // Delete a key
    public void delete(int key) {
        root = delete(root, key);
    }

    private AVLNode delete(AVLNode root, int key) {
        if (root == null) {
            return root;
        }

        if (key < root.key) {
            root.left = delete(root.left, key);
        } else if (key > root.key) {
            root.right = delete(root.right, key);
        } else {
            if (root.left == null || root.right == null) {
                AVLNode temp = (root.left != null) ? root.left : root.right;

                if (temp == null) {
                    temp = root;
                    root = null;
                } else {
                    root = temp;
                }
            } else {
                AVLNode temp = minValueNode(root.right);
                root.key = temp.key;
                root.right = delete(root.right, temp.key);
            }
        }

        if (root == null) {
            return root;
        }

        root.height = 1 + Math.max(height(root.left), height(root.right));

        int balance = getBalance(root);

        // Left Left Case
        if (balance > 1 && getBalance(root.left) >= 0) {
            return rightRotate(root);
        }

        // Left Right Case
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        // Right Right Case
        if (balance < -1 && getBalance(root.right) <= 0) {
            return leftRotate(root);
        }

        // Right Left Case
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    private AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    // Search for a key
    public boolean search(int key) {
        return search(root, key);
    }

    private boolean search(AVLNode root, int key) {
        if (root == null) {
            return false;
        }

        if (root.key == key) {
            return true;
        }

        return key < root.key ? search(root.left, key) : search(root.right, key);
    }

    // For visualization
    public void drawTree(JPanel panel) {
        panel.removeAll();
        if (root != null) {
            drawTree(root, panel, panel.getWidth() / 2, 50, panel.getWidth() / 4);
        }
        panel.revalidate();
        panel.repaint();
    }

    private void drawTree(AVLNode node, JPanel panel, int x, int y, int xOffset) {
        if (node == null) return;

        // Create a visible node representation
        JLabel nodeLabel = new JLabel(String.valueOf(node.key), SwingConstants.CENTER);
        nodeLabel.setOpaque(true);
        nodeLabel.setBackground(Color.WHITE);
        nodeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        nodeLabel.setForeground(Color.BLACK);
        nodeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nodeLabel.setBounds(x - 25, y - 25, 50, 50);
        panel.add(nodeLabel);

        // Draw lines to children
        if (node.left != null) {
            int childX = x - xOffset;
            int childY = y + 80;
            Graphics g = panel.getGraphics();
            g.setColor(Color.BLUE);
            g.drawLine(x, y, childX, childY);
            drawTree(node.left, panel, childX, childY, xOffset / 2);
        }
        if (node.right != null) {
            int childX = x + xOffset;
            int childY = y + 80;
            Graphics g = panel.getGraphics();
            g.setColor(Color.BLUE);
            g.drawLine(x, y, childX, childY);
            drawTree(node.right, panel, childX, childY, xOffset / 2);
        }
    }
}

public class AVLTreeGUI extends JFrame {
    private AVLTree avlTree;
    private JPanel treePanel;
    private JTextField inputField;

    public AVLTreeGUI() {
        avlTree = new AVLTree();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("AVL Tree Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        inputField = new JTextField(10);
        JButton insertButton = new JButton("Insert");
        JButton deleteButton = new JButton("Delete");
        JButton searchButton = new JButton("Search");

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int key = Integer.parseInt(inputField.getText());
                    avlTree.insert(key);
                    System.out.println("Inserted: " + key);
                    avlTree.drawTree(treePanel);
                    inputField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid integer");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int key = Integer.parseInt(inputField.getText());
                    avlTree.delete(key);
                    System.out.println("Deleted: " + key);
                    avlTree.drawTree(treePanel);
                    inputField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid integer");
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int key = Integer.parseInt(inputField.getText());
                    boolean found = avlTree.search(key);
                    JOptionPane.showMessageDialog(null, 
                        found ? "Key " + key + " found in tree" : "Key " + key + " not found in tree");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid integer");
                }
            }
        });

        controlPanel.add(new JLabel("Enter key:"));
        controlPanel.add(inputField);
        controlPanel.add(insertButton);
        controlPanel.add(deleteButton);
        controlPanel.add(searchButton);

        // Tree visualization panel
        treePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(0, 0, 0));
            }
        };
        treePanel.setLayout(null);
        treePanel.setPreferredSize(new Dimension(950, 600));

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(treePanel), BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AVLTreeGUI gui = new AVLTreeGUI();
                gui.setVisible(true);
            }
        });
    }
}
