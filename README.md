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

### **Frontend Stack**
- **JSP** with JSTL for server-side rendering
- **CSS** with shadcn/ui design system
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

## 🎯 Key Features Showcase

### **🔄 Automatic Balance Management**
- Database triggers automatically recalculate balances
- Status updates (Pending → Paid → Overdue) based on payments and due dates
- Transaction rollback support for data integrity

### **🔍 Intelligent Search**
- Multi-field search across debtor names and phone numbers
- Real-time filtering with status-based categories
- Paginated results for optimal performance

## 📚 API Endpoints (Servlets)

- **`/login`** - `LoginServlet` - Authentication and session management
- **`/register`** - `RegisterServlet` - New vendor registration
- **`/home`** - `HomeServlet` - Dashboard data and debt listings
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
---

**Built with ❤️ using Java, Oracle Database, and modern web technologies**
