import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { ConditionTables } from '../condition-tables/condition-tables';

@Component({
  selector: 'app-condizioni-concomitanti',
  imports: [
    MatTabsModule,
    ConditionTables,
  ],
  templateUrl: './condizioni-concomitanti.html',
  styles: ``,
})
export class CondizioniConcomitanti {}
