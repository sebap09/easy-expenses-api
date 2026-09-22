package com.easyexpenses.api.dtos.userprofile;

import java.util.List;

public record Category(Long id, String name, List<SubCategory> subcategories) {
}
