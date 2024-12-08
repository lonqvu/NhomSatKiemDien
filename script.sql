USE [master]
GO
/****** Object:  Database [NhomSatKiemDien]    Script Date: 7/27/2024 11:16:00 AM ******/
CREATE DATABASE [NhomSatKiemDien]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'CuaHangDienTu', FILENAME = N'D:\Project\Share Do An\DATA\DATA\CuaHangDienTu.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'CuaHangDienTu_log', FILENAME = N'D:\Project\Share Do An\DATA\DATA\CuaHangDienTu_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
GO
ALTER DATABASE [NhomSatKiemDien] SET COMPATIBILITY_LEVEL = 130
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [NhomSatKiemDien].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [NhomSatKiemDien] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET ARITHABORT OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [NhomSatKiemDien] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [NhomSatKiemDien] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [NhomSatKiemDien] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET  DISABLE_BROKER 
GO
ALTER DATABASE [NhomSatKiemDien] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [NhomSatKiemDien] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET RECOVERY FULL 
GO
ALTER DATABASE [NhomSatKiemDien] SET  MULTI_USER 
GO
ALTER DATABASE [NhomSatKiemDien] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [NhomSatKiemDien] SET DB_CHAINING OFF 
GO
ALTER DATABASE [NhomSatKiemDien] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [NhomSatKiemDien] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [NhomSatKiemDien] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [NhomSatKiemDien] SET QUERY_STORE = OFF
GO
USE [NhomSatKiemDien]
GO
ALTER DATABASE SCOPED CONFIGURATION SET LEGACY_CARDINALITY_ESTIMATION = OFF;
GO
ALTER DATABASE SCOPED CONFIGURATION SET MAXDOP = 0;
GO
ALTER DATABASE SCOPED CONFIGURATION SET PARAMETER_SNIFFING = ON;
GO
ALTER DATABASE SCOPED CONFIGURATION SET QUERY_OPTIMIZER_HOTFIXES = OFF;
GO
USE [NhomSatKiemDien]
GO
/****** Object:  Table [dbo].[Accounts]    Script Date: 7/27/2024 11:16:00 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Accounts](
	[UserName] [nvarchar](50) NOT NULL,
	[PassWord] [nvarchar](50) NOT NULL,
	[FullName] [nvarchar](50) NOT NULL,
	[RegistDate] [datetime] NULL,
	[RegistPName] [nvarchar](50) NULL,
	[UpdateDate] [datetime] NULL,
	[UpdatePName] [nvarchar](50) NULL,
	[RoleID] [int] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Bill]    Script Date: 7/27/2024 11:16:00 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Bill](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Amount] [decimal](6, 3) NULL,
	[Price] [money] NULL,
	[IntoMoney] [money] NOT NULL,
	[ProductID] [int] NULL,
	[OrderID] [varchar](10) NULL,
	[RegistDate] [datetime] NULL,
	[HeSo] [decimal](6, 3) NULL,
	[SoTam] [int] NULL,
	[TongSoLuong] [decimal](6, 3) NULL,
	[UnitName] [nvarchar](50) NULL,
	[ProductName] [nvarchar](50) NULL,
 CONSTRAINT [PK__Bill_tem__3214EC27ED64C3A1] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Classify]    Script Date: 7/27/2024 11:16:00 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Classify](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Classify] [nvarchar](100) NOT NULL,
 CONSTRAINT [PK_Classify] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Customer]    Script Date: 7/27/2024 11:16:00 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Customer](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[CustomerName] [nvarchar](50) NOT NULL,
	[Address] [nvarchar](50) NULL,
	[PhoneNumber] [varchar](50) NULL,
	[Debt] [money] NULL,
	[OldDebt] [money] NULL,
	[RegistDate] [datetime] NULL,
 CONSTRAINT [PK_Customer] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[InvoiceCounter]    Script Date: 7/27/2024 11:16:00 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[InvoiceCounter](
	[CurrentInvoiceNumber] [int] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Orders]    Script Date: 7/27/2024 11:16:00 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Orders](
	[ID] [varchar](10) NOT NULL,
	[Customer] [nvarchar](50) NULL,
	[Address] [nvarchar](50) NULL,
	[Phone] [varchar](50) NULL,
	[Date] [datetime] NULL,
	[CustomerID] [int] NULL,
	[IsPayed] [bit] NULL,
	[TotalMoneyBill] [money] NULL,
	[OldDebt] [money] NULL,
	[TotalMoneyOrder] [money] NULL,
	[PayMoney] [money] NULL,
	[DebtBack] [money] NULL,
	[StaffName] [nvarchar](50) NULL,
	[Note] [nvarchar](150) NULL,
 CONSTRAINT [PK_Orders] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Products]    Script Date: 7/27/2024 11:16:00 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Products](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[ClassifyID] [int] NOT NULL,
	[Name] [nvarchar](100) NOT NULL,
	[Price] [nchar](100) NOT NULL,
	[RegistDate] [datetime] NULL,
	[UnitName] [nvarchar](50) NULL,
 CONSTRAINT [PK_Products] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Role]    Script Date: 7/27/2024 11:16:00 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Role](
	[ID] [int] NOT NULL,
	[RoleName] [nchar](200) NOT NULL,
	[RegistDateTime] [datetime] NULL,
	[LastUpdateDatetime] [datetime] NULL,
	[RegistPName] [varchar](50) NULL,
	[LastUpdatePName] [varchar](50) NULL,
 CONSTRAINT [PK_Role] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Unit]    Script Date: 7/27/2024 11:16:00 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Unit](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Unit] [nvarchar](50) NULL,
 CONSTRAINT [PK_Unit] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Bill]  WITH CHECK ADD  CONSTRAINT [FK_Bill_Orders] FOREIGN KEY([OrderID])
REFERENCES [dbo].[Orders] ([ID])
GO
ALTER TABLE [dbo].[Bill] CHECK CONSTRAINT [FK_Bill_Orders]
GO
ALTER TABLE [dbo].[Bill]  WITH CHECK ADD  CONSTRAINT [FK_Bill_Products] FOREIGN KEY([ProductID])
REFERENCES [dbo].[Products] ([ID])
GO
ALTER TABLE [dbo].[Bill] CHECK CONSTRAINT [FK_Bill_Products]
GO
ALTER TABLE [dbo].[Orders]  WITH CHECK ADD  CONSTRAINT [FK_Orders_Customer] FOREIGN KEY([CustomerID])
REFERENCES [dbo].[Customer] ([ID])
GO
ALTER TABLE [dbo].[Orders] CHECK CONSTRAINT [FK_Orders_Customer]
GO
ALTER TABLE [dbo].[Products]  WITH CHECK ADD  CONSTRAINT [FK_Products_Classify] FOREIGN KEY([ClassifyID])
REFERENCES [dbo].[Classify] ([ID])
GO
ALTER TABLE [dbo].[Products] CHECK CONSTRAINT [FK_Products_Classify]
GO
USE [master]
GO
ALTER DATABASE [NhomSatKiemDien] SET  READ_WRITE 
GO
