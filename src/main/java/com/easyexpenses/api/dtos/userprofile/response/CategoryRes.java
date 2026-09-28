package com.easyexpenses.api.dtos.userprofile.response;

import java.util.List;

public record CategoryRes(Long id, String name, List<SubCategoryRes> subcategories) {
}
