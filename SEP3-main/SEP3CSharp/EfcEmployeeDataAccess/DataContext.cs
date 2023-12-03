using Microsoft.EntityFrameworkCore;
using Shared.Models;

namespace EfcEmployeeDataAccess; 

public class DataContext : DbContext {
	public DbSet<Employee> Employees { get; set; }
	public DbSet<WarehousePosition> WarehousePositions { get; set; }
	protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder) {
		optionsBuilder.UseNpgsql("Host=mouse.db.elephantsql.com;Username=ylsiahyj;Password=MmTEt0YyYObkMIiBvjtV-yEUTHhr96lO");
	}

	protected override void OnModelCreating(ModelBuilder modelBuilder) {
		modelBuilder.Entity<Employee>().HasIndex(e => e.Username).IsUnique();
		modelBuilder.Entity<Employee>().HasKey(e => e.Id);

		modelBuilder.Entity<WarehousePosition>().HasIndex(e => e.Position).IsUnique();
		modelBuilder.Entity<WarehousePosition>().HasKey(e => e.Id);
	}
}