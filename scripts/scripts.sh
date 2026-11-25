#!/bin/bash

# 커밋 내용 확인
git log --oneline

# 해당 커밋으로 체크아웃
git checkout <commit_id>

# 최신 상태로 돌아오기
git checkout main
