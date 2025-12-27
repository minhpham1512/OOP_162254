package com.bank.view;

import com.bank.model.Card;
import com.bank.model.CreditCard;
import com.bank.model.User;
import com.bank.repository.DatabaseSimulator;
import com.bank.service.AccountService;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Lớp giao diện quản lý Thẻ tín dụng (Credit Card Management)
 * Cho phép người dùng:
 * - Xem danh sách thẻ tín dụng
 * - Xem hạn mức tín dụng và dư nợ
 * - Thanh toán dư nợ
 * - Tạo thẻ tín dụng mới
 */
public class CreditCardManagement extends JPanel {

    private DatabaseSimulator db;
    private User currentUser;

    // Các thành phần UI
    private JTextArea creditCardInfoArea;
    private JList<String> creditCardsList;
    private DefaultListModel<String> cardsListModel;
    private JTextField debtPaymentField;
    private JLabel creditLimitLabel;
    private JLabel currentDebtLabel;
    private JLabel availableCreditLabel;

    public CreditCardManagement(DatabaseSimulator db, User currentUser, AccountService accountService) {
        this.db = db;
        this.currentUser = currentUser;

        // Thiết lập layout chính
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thêm các thành phần
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Cập nhật danh sách thẻ
        refreshCardList();
    }

    /**
     * Tạo panel tiêu đề
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(ThemeColors.PRIMARY);
        
        JLabel titleLabel = new JLabel("QUẢN LÝ THẺ TÍN DỤNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        panel.add(titleLabel);
        
        return panel;
    }

    /**
     * Tạo nội dung chính (gồm 2 phần: danh sách thẻ và chi tiết thẻ)
     */
    private JPanel createMainContent() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Phần trái: Danh sách thẻ tín dụng
        panel.add(createCardListPanel());

        // Phần phải: Chi tiết thẻ và thanh toán nợ
        panel.add(createCardDetailsPanel());

        return panel;
    }

    /**
     * Tạo panel danh sách thẻ tín dụng
     */
    private JPanel createCardListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH THẺ TÍN DỤNG"));

        cardsListModel = new DefaultListModel<>();
        creditCardsList = new JList<>(cardsListModel);
        creditCardsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        creditCardsList.addListSelectionListener(e -> showCardDetail());
        JScrollPane scrollPane = new JScrollPane(creditCardsList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton refreshButton = new JButton("Làm mới");
        JButton blockButton = new JButton("Khóa thẻ");

        buttonPanel.add(refreshButton);
        buttonPanel.add(blockButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện nút Làm mới
        refreshButton.addActionListener(e -> refreshCardList());

        // Xử lý sự kiện nút Khóa thẻ
        blockButton.addActionListener(e -> blockCard());

        return panel;
    }

    /**
     * Tạo panel chi tiết thẻ và thanh toán nợ
     */
    private JPanel createCardDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("CHI TIẾT THẺ & THANH TOÁN NỢ"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hạn mức tín dụng
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Hạn mức tín dụng:"), gbc);
        gbc.gridx = 1;
        creditLimitLabel = new JLabel("0 VND");
        creditLimitLabel.setFont(new Font("Arial", Font.BOLD, 12));
        creditLimitLabel.setForeground(ThemeColors.SUCCESS);
        panel.add(creditLimitLabel, gbc);

        // Dư nợ hiện tại
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Dư nợ hiện tại:"), gbc);
        gbc.gridx = 1;
        currentDebtLabel = new JLabel("0 VND");
        currentDebtLabel.setFont(new Font("Arial", Font.BOLD, 12));
        currentDebtLabel.setForeground(ThemeColors.DANGER);
        panel.add(currentDebtLabel, gbc);

        // Hạn mức còn lại
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Hạn mức còn lại:"), gbc);
        gbc.gridx = 1;
        availableCreditLabel = new JLabel("0 VND");
        availableCreditLabel.setFont(new Font("Arial", Font.BOLD, 12));
        availableCreditLabel.setForeground(ThemeColors.PRIMARY);
        panel.add(availableCreditLabel, gbc);

        // Khoảng trắng
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(new JLabel(""), gbc);
        gbc.gridwidth = 1;

        // Số tiền thanh toán
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Số tiền thanh toán:"), gbc);
        gbc.gridx = 1;
        debtPaymentField = new JTextField(20);
        panel.add(debtPaymentField, gbc);

        // Nút thanh toán
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton payDebtButton = new JButton("Thanh toán dư nợ");
        payDebtButton.setBackground(ThemeColors.SUCCESS);
        payDebtButton.setForeground(ThemeColors.TEXT_PRIMARY);
        payDebtButton.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(payDebtButton, gbc);

        // Xử lý sự kiện nút Thanh toán
        payDebtButton.addActionListener(e -> payDebt());

        // Khoảng trắng
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(new JLabel(""), gbc);

        // Thông tin
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        creditCardInfoArea = new JTextArea(5, 30);
        creditCardInfoArea.setEditable(false);
        creditCardInfoArea.setLineWrap(true);
        creditCardInfoArea.setWrapStyleWord(true);
        creditCardInfoArea.setText("Chọn một thẻ để xem chi tiết");
        creditCardInfoArea.setBackground(ThemeColors.BG_LIGHT);
        creditCardInfoArea.setForeground(ThemeColors.TEXT_PRIMARY);
        JScrollPane scrollPane = new JScrollPane(creditCardInfoArea);
        panel.add(scrollPane, gbc);

        return panel;
    }

    /**
     * Tạo panel chân trang
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea infoArea = new JTextArea(2, 50);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setText("Thông tin: Thẻ tín dụng cho phép bạn chi tiêu lên đến hạn mức được cấp. " +
                         "Bạn cần thanh toán dư nợ hàng tháng để duy trì tín dụng tốt.");
        infoArea.setBackground(ThemeColors.BG_LIGHT);
        infoArea.setForeground(ThemeColors.TEXT_PRIMARY);
        JScrollPane scrollPane = new JScrollPane(infoArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Làm mới danh sách thẻ
     */
    private void refreshCardList() {
        cardsListModel.clear();
        
        // Tìm tất cả thẻ của người dùng hiện tại
        List<Card> allCards = db.getCardsByCustomerId(currentUser.getCustomerId());
        
        for (Card card : allCards) {
            if (card instanceof CreditCard) {
                CreditCard cc = (CreditCard) card;
                String displayText = String.format("Thẻ: %s | Dư nợ: %.2f VND", 
                    cc.getCardNumber(), cc.getCurrentDebt());
                cardsListModel.addElement(displayText);
            }
        }

        if (cardsListModel.isEmpty()) {
            creditCardInfoArea.setText("Bạn chưa có thẻ tín dụng nào. Liên hệ ngân hàng để cấp thẻ.");
            creditLimitLabel.setText("0 VND");
            currentDebtLabel.setText("0 VND");
            availableCreditLabel.setText("0 VND");
        }
    }

    /**
     * Hiển thị chi tiết thẻ được chọn
     */
    private void showCardDetail() {
        int selectedIndex = creditCardsList.getSelectedIndex();
        if (selectedIndex < 0) {
            creditCardInfoArea.setText("Chọn một thẻ để xem chi tiết");
            return;
        }

        List<Card> allCards = db.getCardsByCustomerId(currentUser.getCustomerId());
        int creditCardIndex = 0;
        CreditCard selectedCard = null;

        for (Card card : allCards) {
            if (card instanceof CreditCard) {
                if (creditCardIndex == selectedIndex) {
                    selectedCard = (CreditCard) card;
                    break;
                }
                creditCardIndex++;
            }
        }

        if (selectedCard != null) {
            creditLimitLabel.setText(String.format("%.2f VND", selectedCard.getCreditLimit()));
            currentDebtLabel.setText(String.format("%.2f VND", selectedCard.getCurrentDebt()));
            availableCreditLabel.setText(String.format("%.2f VND", selectedCard.getAvailableCredit()));

            String info = String.format(
                "Số thẻ: %s\n" +
                "Tài khoản: %s\n" +
                "Ngày hết hạn: %s\n" +
                "Trạng thái: %s\n" +
                "Hạn mức: %.2f VND\n" +
                "Dư nợ: %.2f VND\n" +
                "Còn lại: %.2f VND",
                selectedCard.getCardNumber(),
                selectedCard.getAccountNumber(),
                selectedCard.getExpiryDate(),
                selectedCard.getStatus(),
                selectedCard.getCreditLimit(),
                selectedCard.getCurrentDebt(),
                selectedCard.getAvailableCredit()
            );
            creditCardInfoArea.setText(info);
        }
    }

    /**
     * Thanh toán dư nợ
     */
    private void payDebt() {
        int selectedIndex = creditCardsList.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một thẻ trước!", 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (debtPaymentField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền thanh toán!", 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double paymentAmount = Double.parseDouble(debtPaymentField.getText());
            
            if (paymentAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Card> allCards = db.getCardsByCustomerId(currentUser.getCustomerId());
            int creditCardIndex = 0;
            CreditCard selectedCard = null;

            for (Card card : allCards) {
                if (card instanceof CreditCard) {
                    if (creditCardIndex == selectedIndex) {
                        selectedCard = (CreditCard) card;
                        break;
                    }
                    creditCardIndex++;
                }
            }

            if (selectedCard != null) {
                double currentDebt = selectedCard.getCurrentDebt();
                
                if (paymentAmount > currentDebt) {
                    JOptionPane.showMessageDialog(this, 
                        String.format("Số tiền thanh toán vượt quá dư nợ hiện tại (%.2f VND)!", currentDebt),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Giảm dư nợ
                selectedCard.setCurrentDebt(currentDebt - paymentAmount);
                db.saveCard(selectedCard);

                JOptionPane.showMessageDialog(this, 
                    String.format("Thanh toán thành công!\n" +
                                "Dư nợ trước: %.2f VND\n" +
                                "Thanh toán: %.2f VND\n" +
                                "Dư nợ sau: %.2f VND",
                                currentDebt, paymentAmount, selectedCard.getCurrentDebt()),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Xóa form và làm mới
                debtPaymentField.setText("");
                refreshCardList();
                showCardDetail();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!", 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Khóa thẻ
     */
    private void blockCard() {
        int selectedIndex = creditCardsList.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một thẻ trước!", 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Card> allCards = db.getCardsByCustomerId(currentUser.getCustomerId());
        int creditCardIndex = 0;
        CreditCard selectedCard = null;

        for (Card card : allCards) {
            if (card instanceof CreditCard) {
                if (creditCardIndex == selectedIndex) {
                    selectedCard = (CreditCard) card;
                    break;
                }
                creditCardIndex++;
            }
        }

        if (selectedCard != null) {
            selectedCard.setStatus(Card.CardStatus.LOCKED);
            db.saveCard(selectedCard);

            JOptionPane.showMessageDialog(this, "Thẻ đã được khóa thành công!", 
                                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            refreshCardList();
            creditCardInfoArea.setText("Chọn một thẻ để xem chi tiết");
        }
    }
}
