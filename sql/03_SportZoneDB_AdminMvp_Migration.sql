USE SportZoneDB;
GO

SET XACT_ABORT ON;
GO

BEGIN TRANSACTION;

IF COL_LENGTH(N'dbo.Bookings', N'CheckInAt') IS NULL
BEGIN
    ALTER TABLE dbo.Bookings ADD CheckInAt DATETIME NULL;
END;

IF EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'dbo.Bookings')
      AND name = N'UQ_Bookings_Slot_Active'
)
BEGIN
    DROP INDEX UQ_Bookings_Slot_Active ON dbo.Bookings;
END;

IF EXISTS (
    SELECT 1 FROM sys.check_constraints
    WHERE parent_object_id = OBJECT_ID(N'dbo.Bookings')
      AND name = N'CK_Bookings_Status'
)
BEGIN
    ALTER TABLE dbo.Bookings DROP CONSTRAINT CK_Bookings_Status;
END;

ALTER TABLE dbo.Bookings WITH CHECK ADD CONSTRAINT CK_Bookings_Status CHECK (
    Status IN (
        N'Pending', N'Confirmed', N'CheckedIn',
        N'Completed', N'Cancelled', N'NoShow'
    )
);

CREATE UNIQUE INDEX UQ_Bookings_Slot_Active
ON dbo.Bookings (TimeSlotID)
WHERE Status IN (
    N'Pending', N'Confirmed', N'CheckedIn',
    N'Completed', N'NoShow'
);

COMMIT TRANSACTION;
GO

PRINT N'Admin MVP migration applied successfully.';
GO
