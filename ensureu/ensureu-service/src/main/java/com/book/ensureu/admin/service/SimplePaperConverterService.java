package com.book.ensureu.admin.service;

import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.admin.dto.SimplePaperDto;

public interface SimplePaperConverterService {

    PaperCollectionDto convertToFullPaper(SimplePaperDto simplePaperDto);
}
