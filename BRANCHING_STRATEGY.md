# Git Branching Strategy for GPS Tracking App

This document outlines the branching strategy and workflow for the GPS Tracking App development.

## 🌳 Branch Structure

### Main Branches

#### `master` (Production)
- **Purpose**: Contains production-ready code
- **Protection**: Protected branch, requires pull request reviews
- **Deployment**: Automatically deployed to production
- **Merging**: Only from `release/*` or `hotfix/*` branches

#### `development` (Integration)
- **Purpose**: Integration branch for ongoing development
- **Protection**: Protected branch, requires pull request reviews
- **Merging**: Feature branches merge here first
- **Testing**: Integration testing environment

### Supporting Branches

#### `feature/*` (Feature Development)
- **Naming Convention**: `feature/feature-name` (e.g., `feature/user-authentication`)
- **Purpose**: Develop new features or enhancements
- **Source**: Always branched from `development`
- **Destination**: Merged back to `development`
- **Lifecycle**: Deleted after merge

**Example:**
```bash
git checkout development
git pull origin development
git checkout -b feature/offline-mode
# ... develop feature ...
git push -u origin feature/offline-mode
# Create PR to development
```

#### `release/*` (Release Preparation)
- **Naming Convention**: `release/version-number` (e.g., `release/v1.2.0`)
- **Purpose**: Prepare new production releases
- **Source**: Branched from `development`
- **Destination**: Merged to both `master` and `development`
- **Activities**: Bug fixes, version bumping, release notes

**Example:**
```bash
git checkout development
git checkout -b release/v1.2.0
# ... prepare release ...
git push -u origin release/v1.2.0
# Create PRs to master and development
```

#### `hotfix/*` (Critical Bug Fixes)
- **Naming Convention**: `hotfix/issue-description` (e.g., `hotfix/crash-on-startup`)
- **Purpose**: Fix critical bugs in production
- **Source**: Branched from `master`
- **Destination**: Merged to both `master` and `development`
- **Priority**: High priority, immediate deployment

**Example:**
```bash
git checkout master
git checkout -b hotfix/crash-on-startup
# ... fix critical bug ...
git push -u origin hotfix/crash-on-startup
# Create PRs to master and development
```

## 🔄 Workflow Process

### Feature Development Workflow

1. **Start Feature**
   ```bash
   git checkout development
   git pull origin development
   git checkout -b feature/your-feature-name
   ```

2. **Develop Feature**
   - Make commits with clear messages
   - Push regularly to remote
   - Keep feature branch up to date with development

3. **Complete Feature**
   ```bash
   git push -u origin feature/your-feature-name
   # Create Pull Request to development
   ```

4. **After Merge**
   ```bash
   git checkout development
   git pull origin development
   git branch -d feature/your-feature-name
   git push origin --delete feature/your-feature-name
   ```

### Release Workflow

1. **Create Release Branch**
   ```bash
   git checkout development
   git checkout -b release/v1.2.0
   ```

2. **Prepare Release**
   - Update version numbers
   - Update CHANGELOG.md
   - Fix any release-blocking bugs
   - Test thoroughly

3. **Merge Release**
   ```bash
   # Create PR to master
   # Create PR to development
   # Tag the release
   git tag -a v1.2.0 -m "Release version 1.2.0"
   git push origin v1.2.0
   ```

### Hotfix Workflow

1. **Create Hotfix Branch**
   ```bash
   git checkout master
   git checkout -b hotfix/critical-bug-fix
   ```

2. **Fix and Test**
   - Fix the critical issue
   - Test thoroughly
   - Update version number

3. **Deploy Hotfix**
   ```bash
   # Create PR to master
   # Create PR to development
   # Tag the hotfix
   git tag -a v1.1.1 -m "Hotfix version 1.1.1"
   git push origin v1.1.1
   ```

## 📋 Branch Protection Rules

### Master Branch
- ✅ Require pull request reviews (2 reviewers)
- ✅ Require status checks to pass
- ✅ Require branches to be up to date
- ✅ Restrict pushes to master
- ✅ Allow force pushes: ❌
- ✅ Allow deletions: ❌

### Development Branch
- ✅ Require pull request reviews (1 reviewer)
- ✅ Require status checks to pass
- ✅ Require branches to be up to date
- ✅ Restrict pushes to development
- ✅ Allow force pushes: ❌
- ✅ Allow deletions: ❌

## 🏷️ Tagging Strategy

### Version Numbering
- **Format**: `vMAJOR.MINOR.PATCH` (e.g., `v1.2.3`)
- **MAJOR**: Breaking changes
- **MINOR**: New features, backward compatible
- **PATCH**: Bug fixes, backward compatible

### Tag Creation
```bash
# Create annotated tag
git tag -a v1.2.0 -m "Release version 1.2.0"

# Push tag to remote
git push origin v1.2.0

# Push all tags
git push origin --tags
```

## 📝 Commit Message Convention

### Format
```
type(scope): description

[optional body]

[optional footer]
```

### Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

### Examples
```
feat(tracking): add real-time speed calculation
fix(permissions): resolve location permission dialog issue
docs(readme): update installation instructions
refactor(ui): improve component structure
```

## 🚀 Quick Reference Commands

### Branch Management
```bash
# List all branches
git branch -a

# Switch to branch
git checkout branch-name

# Create and switch to new branch
git checkout -b new-branch-name

# Delete local branch
git branch -d branch-name

# Delete remote branch
git push origin --delete branch-name
```

### Sync with Remote
```bash
# Fetch all remote changes
git fetch origin

# Pull latest changes
git pull origin branch-name

# Push to remote
git push origin branch-name
```

### Merge and Rebase
```bash
# Merge branch into current branch
git merge branch-name

# Rebase current branch onto another
git rebase branch-name

# Interactive rebase (last 3 commits)
git rebase -i HEAD~3
```

## 📚 Additional Resources

- [Git Flow](https://nvie.com/posts/a-successful-git-branching-model/)
- [GitHub Flow](https://guides.github.com/introduction/flow/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Semantic Versioning](https://semver.org/)

---

**Note**: This branching strategy is designed for collaborative development and follows industry best practices for Android app development.
