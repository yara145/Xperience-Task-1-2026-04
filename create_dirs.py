import os
import sys

# Base path for the Spring Boot project
base_path = r'C:\Users\97250\Xperience-Task-1-2026-04\hero-backend\src\main\java\com\xperience\hero'

# Subdirectories to create
subdirs = ['model', 'service', 'repository', 'controller', 'dto']

# Create subdirectories
print("Creating directories...\n")
for subdir in subdirs:
    dir_path = os.path.join(base_path, subdir)
    try:
        os.makedirs(dir_path, exist_ok=True)
        print(f"✓ Created: {subdir}")
    except Exception as e:
        print(f"✗ Error creating {subdir}: {e}")

print("\n===== DIRECTORY STRUCTURE CREATED =====\n")
print(f"Base directory: {base_path}\n")
print("Contents:\n")

# List all subdirectories and their contents
if os.path.exists(base_path):
    items = sorted(os.listdir(base_path))
    for item in items:
        item_path = os.path.join(base_path, item)
        if os.path.isdir(item_path):
            print(f"📁 {item}/")
            subfiles = sorted(os.listdir(item_path))
            if len(subfiles) > 0:
                for i, subfile in enumerate(subfiles):
                    if i == len(subfiles) - 1:
                        print(f"   └─ {subfile}")
                    else:
                        print(f"   ├─ {subfile}")
            else:
                print("   (empty directory)")
            print()
        else:
            # Only show Java files that are directly in the hero directory
            if item.endswith('.java'):
                print(f"📄 {item}")

print("\n===== VERIFICATION =====\n")
for subdir in subdirs:
    dir_path = os.path.join(base_path, subdir)
    exists = "✓ exists" if os.path.exists(dir_path) else "✗ NOT FOUND"
    print(f"{subdir:12} {exists}")

print("\n===== FULL TREE VIEW =====\n")
print(f"{os.path.basename(base_path)}/")
for subdir in sorted(subdirs):
    dir_path = os.path.join(base_path, subdir)
    print(f"└── {subdir}/")
