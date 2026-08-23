-- =============================================================================
-- SportZoneDB — Schema (Sports Complex: Tennis + Pickleball)
-- PRJ301 Individual Assignment
-- Run this file first, then: 02_SportZoneDB_Seed.sql
-- SQL Server (Express / Developer / LocalDB)
-- =============================================================================

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_PADDING ON;
GO

USE master;
GO

IF EXISTS (SELECT 1 FROM sys.databases WHERE name = N'SportZoneDB')
BEGIN
    ALTER DATABASE SportZoneDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE SportZoneDB;
END
GO

CREATE DATABASE SportZoneDB;
GO

USE SportZoneDB;
GO

-- =============================================================================
-- 1. ROLES
-- =============================================================================
CREATE TABLE Roles (
    RoleID      INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    RoleName    NVARCHAR(50)      NOT NULL,
    CONSTRAINT UQ_Roles_RoleName UNIQUE (RoleName)
);
GO

-- =============================================================================
-- 2. ACCOUNTS
-- PasswordHash: SHA-256 hex (64 chars) or BCrypt — app must NOT store plain text
-- =============================================================================
CREATE TABLE Accounts (
    AccountID       INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    RoleID          INT               NOT NULL,
    Email           NVARCHAR(100)     NOT NULL,
    Phone           NVARCHAR(20)      NOT NULL,
    PasswordHash    NVARCHAR(255)     NOT NULL,
    FullName        NVARCHAR(100)     NOT NULL,
    Status          NVARCHAR(30)      NOT NULL CONSTRAINT DF_Accounts_Status DEFAULT (N'Active'),
    LastLoginAt     DATETIME          NULL,
    CreatedAt       DATETIME          NOT NULL CONSTRAINT DF_Accounts_CreatedAt DEFAULT (GETDATE()),

    CONSTRAINT FK_Accounts_Roles FOREIGN KEY (RoleID) REFERENCES Roles(RoleID),
    CONSTRAINT UQ_Accounts_Email UNIQUE (Email),
    CONSTRAINT UQ_Accounts_Phone UNIQUE (Phone),
    CONSTRAINT CK_Accounts_Status CHECK (Status IN (N'Active', N'Frozen', N'Pending'))
);
GO

CREATE INDEX IX_Accounts_RoleID ON Accounts(RoleID);
CREATE INDEX IX_Accounts_Status ON Accounts(Status);
GO

-- =============================================================================
-- 3. COURT TYPES (fixed: Tennis, Pickleball)
-- =============================================================================
CREATE TABLE CourtTypes (
    CourtTypeID     INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    TypeName        NVARCHAR(50)      NOT NULL,
    Description     NVARCHAR(255)     NULL,
    IsActive        BIT               NOT NULL CONSTRAINT DF_CourtTypes_IsActive DEFAULT (1),

    CONSTRAINT UQ_CourtTypes_TypeName UNIQUE (TypeName)
);
GO

-- =============================================================================
-- 4. COURTS (fixed count in seed: 5 Tennis + 5 Pickleball)
-- =============================================================================
CREATE TABLE Courts (
    CourtID         INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    CourtTypeID     INT               NOT NULL,
    CourtName       NVARCHAR(50)      NOT NULL,
    Location        NVARCHAR(100)     NULL,
    PricePerHour    DECIMAL(18,0)     NOT NULL,
    Description     NVARCHAR(255)     NULL,
    ImageURL        NVARCHAR(255)     NULL,
    Status          NVARCHAR(30)      NOT NULL CONSTRAINT DF_Courts_Status DEFAULT (N'Available'),
    IsActive        BIT               NOT NULL CONSTRAINT DF_Courts_IsActive DEFAULT (1),
    CreatedAt       DATETIME          NOT NULL CONSTRAINT DF_Courts_CreatedAt DEFAULT (GETDATE()),

    CONSTRAINT FK_Courts_CourtTypes FOREIGN KEY (CourtTypeID) REFERENCES CourtTypes(CourtTypeID),
    CONSTRAINT UQ_Courts_CourtName UNIQUE (CourtName),
    CONSTRAINT CK_Courts_Price CHECK (PricePerHour >= 0),
    CONSTRAINT CK_Courts_Status CHECK (Status IN (N'Available', N'Maintenance', N'Unavailable'))
);
GO

CREATE INDEX IX_Courts_CourtTypeID ON Courts(CourtTypeID);
CREATE INDEX IX_Courts_Status ON Courts(Status);
GO

-- =============================================================================
-- 5. TIME SLOTS (per court, per date)
-- =============================================================================
CREATE TABLE TimeSlots (
    TimeSlotID      INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    CourtID         INT               NOT NULL,
    SlotDate        DATE              NOT NULL,
    StartTime       TIME(0)           NOT NULL,
    EndTime         TIME(0)           NOT NULL,
    Status          NVARCHAR(30)      NOT NULL CONSTRAINT DF_TimeSlots_Status DEFAULT (N'Available'),
    CreatedAt       DATETIME          NOT NULL CONSTRAINT DF_TimeSlots_CreatedAt DEFAULT (GETDATE()),

    CONSTRAINT FK_TimeSlots_Courts FOREIGN KEY (CourtID) REFERENCES Courts(CourtID),
    CONSTRAINT CK_TimeSlots_TimeRange CHECK (EndTime > StartTime),
    CONSTRAINT CK_TimeSlots_Status CHECK (Status IN (N'Available', N'Booked', N'Closed')),
    CONSTRAINT UQ_TimeSlots_Court_Date_Start UNIQUE (CourtID, SlotDate, StartTime)
);
GO

CREATE INDEX IX_TimeSlots_SlotDate ON TimeSlots(SlotDate);
CREATE INDEX IX_TimeSlots_Court_Date ON TimeSlots(CourtID, SlotDate);
CREATE INDEX IX_TimeSlots_Status ON TimeSlots(Status);
GO

-- =============================================================================
-- 6. SERVICES (add-ons / packages; court price is on Courts)
-- =============================================================================
CREATE TABLE Services (
    ServiceID           INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    ServiceName         NVARCHAR(100)     NOT NULL,
    Description         NVARCHAR(255)     NULL,
    Price               DECIMAL(18,0)     NOT NULL,
    DurationMinutes     INT               NOT NULL CONSTRAINT DF_Services_Duration DEFAULT (60),
    IsActive            BIT               NOT NULL CONSTRAINT DF_Services_IsActive DEFAULT (1),
    CreatedAt           DATETIME          NOT NULL CONSTRAINT DF_Services_CreatedAt DEFAULT (GETDATE()),

    CONSTRAINT UQ_Services_ServiceName UNIQUE (ServiceName),
    CONSTRAINT CK_Services_Price CHECK (Price >= 0),
    CONSTRAINT CK_Services_Duration CHECK (DurationMinutes > 0)
);
GO

-- =============================================================================
-- 7. BOOKINGS
-- =============================================================================
CREATE TABLE Bookings (
    BookingID           INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    AccountID           INT               NOT NULL,
    TimeSlotID          INT               NOT NULL,  -- court is derived via TimeSlots.CourtID (no direct CourtID FK)

    CourtPriceAtOrder   DECIMAL(18,0)     NOT NULL,  -- snapshot from Courts.PricePerHour
    ServiceTotalAtOrder DECIMAL(18,0)     NOT NULL CONSTRAINT DF_Bookings_ServiceTotal DEFAULT (0),
    TotalAmount         DECIMAL(18,0)     NOT NULL,

    Status              NVARCHAR(30)      NOT NULL CONSTRAINT DF_Bookings_Status DEFAULT (N'Pending'),
    Notes               NVARCHAR(255)     NULL,
    CancelReason        NVARCHAR(255)     NULL,
    BookingDate         DATETIME          NOT NULL CONSTRAINT DF_Bookings_BookingDate DEFAULT (GETDATE()),
    UpdatedAt           DATETIME          NULL,

    CONSTRAINT FK_Bookings_Accounts FOREIGN KEY (AccountID) REFERENCES Accounts(AccountID),
    CONSTRAINT FK_Bookings_TimeSlots FOREIGN KEY (TimeSlotID) REFERENCES TimeSlots(TimeSlotID),
    CONSTRAINT CK_Bookings_Amounts CHECK (
        CourtPriceAtOrder >= 0
        AND ServiceTotalAtOrder >= 0
        AND TotalAmount >= 0
        AND TotalAmount = CourtPriceAtOrder + ServiceTotalAtOrder
    ),
    CONSTRAINT CK_Bookings_Status CHECK (
        Status IN (N'Pending', N'Confirmed', N'Completed', N'Cancelled', N'NoShow')
    )
);
GO

CREATE INDEX IX_Bookings_AccountID ON Bookings(AccountID);
CREATE INDEX IX_Bookings_TimeSlotID ON Bookings(TimeSlotID);
CREATE INDEX IX_Bookings_Status ON Bookings(Status);
CREATE INDEX IX_Bookings_BookingDate ON Bookings(BookingDate);
GO

-- One active booking per time slot (prevent double booking).
-- TimeSlotID alone identifies court+date+hour, so no CourtID needed here.
-- Requires QUOTED_IDENTIFIER ON (set at top of script)
SET QUOTED_IDENTIFIER ON;
GO
CREATE UNIQUE INDEX UQ_Bookings_Slot_Active
ON Bookings (TimeSlotID)
WHERE Status IN (N'Pending', N'Confirmed', N'Completed', N'NoShow');
GO

-- SCHEMA_CONTRACT_BOOKINGS_V3:
-- Bookings derives its court only through TimeSlotID; direct CourtID is forbidden.
IF COL_LENGTH(N'dbo.Bookings', N'TimeSlotID') IS NULL
    THROW 51001, N'Invalid Bookings schema: TimeSlotID is required.', 1;
IF COL_LENGTH(N'dbo.Bookings', N'CourtID') IS NOT NULL
    THROW 51002, N'Invalid Bookings schema: direct CourtID must not exist.', 1;
GO

-- =============================================================================
-- 8. BOOKING SERVICES (line items / add-ons)
-- =============================================================================
CREATE TABLE BookingServices (
    BookingID           INT               NOT NULL,
    ServiceID           INT               NOT NULL,
    Quantity            INT               NOT NULL CONSTRAINT DF_BookingServices_Qty DEFAULT (1),
    PriceAtOrder        DECIMAL(18,0)     NOT NULL,
    LineTotal           AS (Quantity * PriceAtOrder) PERSISTED,

    CONSTRAINT PK_BookingServices PRIMARY KEY (BookingID, ServiceID),
    CONSTRAINT FK_BookingServices_Bookings FOREIGN KEY (BookingID) REFERENCES Bookings(BookingID),
    CONSTRAINT FK_BookingServices_Services FOREIGN KEY (ServiceID) REFERENCES Services(ServiceID),
    CONSTRAINT CK_BookingServices_Qty CHECK (Quantity > 0),
    CONSTRAINT CK_BookingServices_Price CHECK (PriceAtOrder >= 0)
);
GO

CREATE INDEX IX_BookingServices_ServiceID ON BookingServices(ServiceID);
GO

-- =============================================================================
-- 9. INVOICES + PAYMENT
-- At most one invoice per booking (1:0..1). Invoice is created when collection starts, not at booking.
-- =============================================================================
CREATE TABLE Invoices (
    InvoiceID           INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    BookingID           INT               NOT NULL,
    InvoiceCode         NVARCHAR(30)      NOT NULL,
    InvoiceDate         DATETIME          NOT NULL CONSTRAINT DF_Invoices_InvoiceDate DEFAULT (GETDATE()),

    SubTotal            DECIMAL(18,0)     NOT NULL,
    DiscountAmount      DECIMAL(18,0)     NOT NULL CONSTRAINT DF_Invoices_Discount DEFAULT (0),
    FinalAmount         DECIMAL(18,0)     NOT NULL,

    PaymentStatus       NVARCHAR(30)      NOT NULL CONSTRAINT DF_Invoices_PayStatus DEFAULT (N'Unpaid'),
    PaymentMethod       NVARCHAR(30)      NULL,
    PaidAt              DATETIME          NULL,
    PaymentNote         NVARCHAR(255)     NULL,  -- e.g. transfer reference / user notice

    Note                NVARCHAR(255)     NULL,
    CreatedAt           DATETIME          NOT NULL CONSTRAINT DF_Invoices_CreatedAt DEFAULT (GETDATE()),
    UpdatedAt           DATETIME          NULL,

    CONSTRAINT FK_Invoices_Bookings FOREIGN KEY (BookingID) REFERENCES Bookings(BookingID),
    CONSTRAINT UQ_Invoices_BookingID UNIQUE (BookingID),
    CONSTRAINT UQ_Invoices_InvoiceCode UNIQUE (InvoiceCode),
    CONSTRAINT CK_Invoices_Amounts CHECK (
        SubTotal >= 0
        AND DiscountAmount >= 0
        AND FinalAmount >= 0
        AND FinalAmount = SubTotal - DiscountAmount
    ),
    CONSTRAINT CK_Invoices_PaymentStatus CHECK (
        PaymentStatus IN (N'Unpaid', N'Paid', N'Cancelled')
    ),
    CONSTRAINT CK_Invoices_PaymentMethod CHECK (
        PaymentMethod IS NULL
        OR PaymentMethod IN (N'Cash', N'BankTransfer')
    )
);
GO

CREATE INDEX IX_Invoices_PaymentStatus ON Invoices(PaymentStatus);
CREATE INDEX IX_Invoices_InvoiceDate ON Invoices(InvoiceDate);
GO

PRINT N'SportZoneDB schema created successfully. Next: run 02_SportZoneDB_Seed.sql';
GO
