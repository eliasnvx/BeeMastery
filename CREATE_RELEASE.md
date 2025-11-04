# How to Create GitHub Release v1.0.1

## Automatic Method (if you have GitHub CLI)

```bash
gh release create v1.0.1 \
  --title "Bee Mastery v1.0.1 - JEI Integration Update" \
  --notes-file release_notes_v1.0.1.md \
  build/libs/beemastery_1.20.1-1.0.1.jar
```

## Manual Method (via GitHub Web Interface)

1. **Go to Releases Page:**
   - Navigate to: https://github.com/eliasnvx/BeeMastery/releases

2. **Click "Draft a new release"**

3. **Fill in the details:**
   - **Tag**: `v1.0.1` (should already exist)
   - **Title**: `Bee Mastery v1.0.1 - JEI Integration Update`
   - **Description**: Copy content from `release_notes_v1.0.1.md`

4. **Upload JAR file:**
   - Drag and drop: `build/libs/beemastery_1.20.1-1.0.1.jar`

5. **Click "Publish release"**

---

## Quick Links

- **GitHub Releases**: https://github.com/eliasnvx/BeeMastery/releases/new
- **JAR Location**: `build/libs/beemastery_1.20.1-1.0.1.jar`
- **Release Notes**: `release_notes_v1.0.1.md`
