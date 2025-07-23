# 🏪 Vendor Debtors Management System

A comprehensive web-based debt management system built with **Java Servlets**, **JSP**, and **Oracle Database**. This application enables vendors to efficiently track, manage, and monitor customer debts with real-time payment processing and detailed analytics.

## ✨ Key Features

### 🔐 **Authentication & User Management**
- Secure vendor registration and login system
- Session-based authentication with logout functionality
- User profile management with phone and contact details

### 💰 **Debt Management**
- **Create new debts** with debtor information, amounts, and due dates
- **Track payment status** (Pending, Paid, Overdue)
- **Automatic balance calculations** with real-time updates
- **Market-based organization** for better debt categorization

### 💳 **Transaction Processing**
- **Credit transactions** - Record payments from debtors
- **Debit transactions** - Add additional charges or fees
- **Transaction history** with detailed logs and descriptions
- **Automatic balance updates** via database triggers

### 📊 **Analytics & Reporting**
- **Real-time dashboard** with payment percentage visualizations
- **Interactive progress bars** showing debt payment completion
- **Detailed statistics** with date range filtering
- **Summary metrics**: Total debts, amounts, balances, and overdue items

### 🔍 **Advanced Search & Filtering**
- Search debts by debtor name or phone number
- Filter by payment status (Pending, Paid, Overdue)
- Pagination support for large datasets
- Real-time search with dynamic results

## 🏗️ Technical Architecture

### **Backend Stack**
- **Java 17** with Jakarta Servlets
- **Oracle Database** with advanced PL/SQL procedures
- **HikariCP** connection pooling for optimal performance
- **GSON** for JSON data processing

### **Frontend Stack**
- **JSP** with JSTL for server-side rendering
- **Modern CSS** with shadcn/ui design system
- **Responsive design** with mobile-first approach
- **Interactive animations** and progress visualizations

## 🗃️ Database Schema

### **Core Tables**
- **`VENDORS`** - Vendor authentication and profile information
- **`MARKETS`** - Market categorization for debt organization
- **`DEBTS`** - Main debt records with debtor details and amounts
- **`DEBT_TRANSACTION`** - Payment and transaction history

### **Oracle PL/SQL Procedures & Functions**

#### **📊 Statistics Functions**
- **`get_vendor_total_debt(p_vendor_id)`** - Calculate total debt amount for a vendor
- **`get_vendor_total_balance(p_vendor_id)`** - Get current outstanding balance
- **`get_overdue_debts_total(p_vendor_id)`** - Calculate total overdue amounts
- **`get_debt_stats_by_date(p_vendor_id, p_start_date, p_end_date)`** - Comprehensive statistics with date filtering

#### **🔍 Data Retrieval Procedures**
- **`search_debts(p_debtor_phone, p_debtor_name, p_vendor_id, p_status)`** - Advanced debt search with multiple filters
- **`get_debt_transactions(p_debt_id)`** - Retrieve transaction history for specific debts

### **🤖 Database Triggers**
- **`TRG_UPDATE_DEBT_AFTER_TRANSACTION`** - Automatically updates debt balances and status when transactions are added, modified, or removed

## 🎨 Web Interface (JSP Pages)

### **📱 Core Pages**
- **`login.jsp`** - Vendor authentication with modern gradient design
- **`register.jsp`** - New vendor registration form
- **`home.jsp`** - Main dashboard with statistics and debt listing

### **💼 Debt Management Pages**
- **`add_debt.jsp`** - Create new debt entries with validation
- **`debt_info.jsp`** - Detailed debt view with tabbed interface (Info, Transactions, Edit)
- **`add_transaction.jsp`** - Record new credit/debit transactions

### **📈 Analytics Pages**
- **`detailed-stats.jsp`** - Advanced statistics modal with date range selection

## 🚀 Getting Started

### **Prerequisites**
- Java 17 or higher
- Oracle Database (19c or later)
- Apache Tomcat 10+ or similar servlet container
- Maven 3.6+

### **Installation**

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/vendor-debtors.git
cd vendor-debtors
```

2. **Setup Oracle Database**
```sql
-- Run the database schema
@db.sql
```

3. **Configure database connection**
Update your database connection settings in the servlet configuration.

4. **Build and deploy**
```bash
mvn clean package
# Deploy the generated WAR file to your servlet container
```

## 🎯 Key Features Showcase

### **📊 Dynamic Progress Visualization**
- Battery-like progress bars showing payment completion percentage
- Shimmer animations for enhanced user experience
- Real-time updates when payments are processed

### **🔄 Automatic Balance Management**
- Database triggers automatically recalculate balances
- Status updates (Pending → Paid → Overdue) based on payments and due dates
- Transaction rollback support for data integrity

### **📱 Responsive Design**
- Mobile-first CSS architecture
- Modern UI components with shadcn/ui design principles
- Smooth animations and hover effects
- Accessible form controls and navigation

### **🔍 Intelligent Search**
- Multi-field search across debtor names and phone numbers
- Real-time filtering with status-based categories
- Paginated results for optimal performance

## 📚 API Endpoints (Servlets)

- **`/login`** - `LoginServlet` - Authentication and session management
- **`/register`** - `RegisterServlet` - New vendor registration
- **`/home`** - `HomeServlet` - Dashboard data and debt listings
- **`/debt`** - `DebtServlet` - CRUD operations for debt management
- **`/debt-transaction`** - `DebtTransactionServlet` - Transaction processing
- **`/logout`** - `LogoutServlet` - Session termination

## 🎨 Design System

The application uses a modern design system with:
- **CSS Custom Properties** for consistent theming
- **Gradient backgrounds** and glassmorphism effects
- **Micro-animations** for enhanced user experience
- **Responsive grid layouts** for optimal viewing on all devices

## 🔧 Database Features

### **Advanced Oracle Capabilities**
- **PL/SQL functions** for complex business logic
- **Database triggers** for automatic data consistency
- **Optimized queries** with proper indexing
- **Transaction safety** with rollback support

### **Performance Optimizations**
- **HikariCP connection pooling** for database efficiency
- **Prepared statements** to prevent SQL injection
- **Cursor-based result sets** for memory efficiency
- **Function-based indexes** for case-insensitive search optimization:
  - `idx_debts_debtor_name_upper` - Fast debtor name searches
  - `idx_debts_debtor_phone_upper` - Optimized phone number lookups

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions, please open an issue in the GitHub repository.

---

**Built with ❤️ using Java, Oracle Database, and modern web technologies**